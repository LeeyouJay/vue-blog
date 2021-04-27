package com.arslinth.utils;

import com.arslinth.entity.ImageResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@Slf4j
public class SliderImgUtil {


    //图片的路径
    private  String basePath;

    //图片的最大大小
    private  static int IMAGE_MAX_WIDTH = 300;
    private  static int IMAGE_MAX_HEIGHT = 260;
    //抠图上面的半径
    private   static int RADIUS = IMAGE_MAX_WIDTH/20;
    //抠图区域的高度
    private   static int CUT_HEIGHT = IMAGE_MAX_WIDTH/5;
    //抠图区域的宽度
    private   static int CUT_WIDTH = IMAGE_MAX_WIDTH/5;
    //被扣地方填充的颜色
    private   static int FLAG = 0x778899;

    //抠图部分凸起的方向
    private  Location location;

    ImageResult imageResult = new ImageResult();

    //
    private  String ORI_IMAGE_KEY = "ORI_IMAGE_KEY";
    private  String CUT_IMAGE_KEY = "CUT_IMAGE_KEY";

    //抠图区的原点坐标(x0,y0)

    /*
      (x0,y0)         (xMax,y0)
        ****************
        *              *
        *              *
        *              *
        ****************
       (x0,yMax)　　　(xMax,yMax)
    */
    private  int XPOS;
    private  int YPOS;

    @Data
    private  class ImageMessage{
        private int xpos;
        private int ypos;
        private int cutImageWidth;
        private int cutImageHeight;

    }

    ImageMessage imageMessage = new ImageMessage();

    public SliderImgUtil(String basePath) {
        this.basePath = basePath;
    }

    //获取抠图区的坐标原点
    public  void createXYPos(BufferedImage oriImage){

        int height = oriImage.getHeight();
        int width = oriImage.getWidth();


        XPOS = new Random().nextInt(width-CUT_WIDTH-RADIUS);
        YPOS = new Random().nextInt(height-CUT_HEIGHT-RADIUS);

        //确保横坐标位于2/4－－3/4
        int div = (IMAGE_MAX_WIDTH/4);

        if(XPOS/div ==  0 ){
            XPOS = XPOS + div*2;
        }
        else if(XPOS/div ==  1 ){
            XPOS = XPOS + div;
        }
        else if(XPOS/div ==  3 ){
            XPOS = XPOS - div;
        }
        //当然切割半径大于横坐标重新计算
        if(RADIUS>=YPOS){
            createXYPos(oriImage);
        }

    }


    //对外提供的接口
    public  ImageResult imageResult() throws IOException{
        return imageResult(getRandomImage(basePath+"sliderImg"));
    }

    public ImageResult imageResult(File file) throws IOException {


        
        log.info("file = {}",file.getName());

        BufferedImage oriBufferedImage = getBufferedImage(file);

        //检测图片大小
        oriBufferedImage = checkImage(oriBufferedImage);

        //初始化原点坐标
        createXYPos(oriBufferedImage);
        //获取被扣图像的标志图
        int[][] blockData = getBlockData(oriBufferedImage);
        //printBlockData(blockData);

        //计算抠图区域的信息
        createImageMessage();

        //获取扣了图的原图和被扣部分的图
        Map<String,BufferedImage> imageMap =  cutByTemplate(oriBufferedImage,blockData);


        imageResult.setOriImage(ImageBase64(imageMap.get(ORI_IMAGE_KEY)));
        imageResult.setCutImage(ImageBase64(imageMap.get(CUT_IMAGE_KEY)));

        imageResult.setXpos(imageMessage.getXpos());
        imageResult.setYpos(imageMessage.getYpos());
        imageResult.setCutImageWidth(imageMessage.getCutImageWidth());
        imageResult.setCutImageHeight(imageMessage.getCutImageHeight());

        return imageResult;
    }
    //计算抠图的相关参数
    private  void createImageMessage(){

        int x = 0,y = 0;
        int w = 0, h = 0;


        if(location == Location.UP){
            x = XPOS;
            y = YPOS - RADIUS;

            w = CUT_WIDTH;
            h = CUT_HEIGHT + RADIUS;
        }
        else if(location == Location.LEFT){
            x = XPOS-RADIUS;
            y = YPOS;

            w = CUT_WIDTH + RADIUS;
            h = CUT_HEIGHT;
        }

        else if(location == Location.DOWN){
            x = XPOS;
            y = YPOS;

            w = CUT_WIDTH;
            h = CUT_HEIGHT + RADIUS;
        }

        if(location == Location.RIGHT){

            x = XPOS;
            y = YPOS;

            w = CUT_WIDTH + RADIUS;
            h = CUT_HEIGHT;
        }

        imageMessage.setXpos(x);
        imageMessage.setYpos(y);
        imageMessage.setCutImageHeight(h);
        imageMessage.setCutImageWidth(w);
    }
    //检测图片大小是否符合要求
    private  BufferedImage checkImage(BufferedImage image) throws IOException {

        if((image.getWidth()  == IMAGE_MAX_WIDTH)
            || (image.getHeight()  == IMAGE_MAX_HEIGHT)){
            log.info("图片大小符合要求");
            return image;
        }

        else if((image.getWidth()  < IMAGE_MAX_WIDTH)
                || (image.getHeight()  < IMAGE_MAX_HEIGHT)){
            log.info("图片太小．不符合要求w*h[300*240]");
            throw  new IllegalArgumentException("图片太小．不符合要求w*h[300*240]");
        }

        else {
            log.info("压缩图片");
            return compressImage(image,IMAGE_MAX_WIDTH,IMAGE_MAX_HEIGHT);
            //ImageIO.write(compress,"jpg",new File(basePathOutput+"compress-1.png"));

        }


    }

    //使被扣地方之外变为透明色
    //image：图片　，　blockData：　抠图数据
    public  BufferedImage drawTransparent(BufferedImage image,int[][] blockData) throws IOException{

        int width = image.getWidth();
        int height = image.getHeight();
        int type = image.getType();

        BufferedImage parentImage = new BufferedImage(width,height,type);

        // 获取Graphics2D
        Graphics2D g2d = parentImage.createGraphics();

        //透明化整张图
        parentImage = g2d.getDeviceConfiguration()
                .createCompatibleImage(width, height, Transparency.BITMASK);
        g2d.dispose();
        g2d = parentImage.createGraphics();
        // 背景透明代码结束

        //重新对扣出来的部分进行填充原图的rgb
        for(int x = 0; x< image.getWidth();x++){
            for(int y = 0; y< image.getHeight(); y++){
                if (blockData[x][y] == FLAG){

                    int rgb = image.getRGB(x,y);
                   // log.info("rgb = " + rgb);
                    int b = (0xff & rgb);
                    int g = (0xff & (rgb >> 8));
                    int r = (0xff & (rgb >> 16));

                    g2d.setColor(new Color(r, g, b));
                    g2d.setStroke(new BasicStroke(1f));
                    g2d.fillRect(x, y, 1, 1);

                }
            }
        }

        // 释放对象
        g2d.dispose();

        return parentImage;
    }

    private Color color(int rgb){
        // log.info("rgb = " + rgb);
        int b = (0xff & rgb);
        int g = (0xff & (rgb >> 8));
        int r = (0xff & (rgb >> 16));
        return new Color(r, g, b);
    }


    //获取抠完图的原图和被扣出来的图
    public  Map<String,BufferedImage> cutByTemplate(BufferedImage oriImage,  int[][] blockData){


        Map<String,BufferedImage> imgMap = new HashMap<>();

        BufferedImage cutImage = new BufferedImage(imageMessage.cutImageWidth,imageMessage.cutImageHeight,oriImage.getType());

        // 获取Graphics2D
        Graphics2D g2d = cutImage.createGraphics();

        //透明化整张图
        cutImage = g2d.getDeviceConfiguration()
                .createCompatibleImage(imageMessage.cutImageWidth,imageMessage.cutImageHeight, Transparency.BITMASK);
        g2d.dispose();
        g2d = cutImage.createGraphics();
        // 背景透明代码结束


        log.info("imageMessage = {}",imageMessage);
        int xmax = imageMessage.xpos + imageMessage.cutImageWidth;
        int ymax = imageMessage.ypos + imageMessage.cutImageHeight;
        if (imageMessage.xpos<0)imageMessage.xpos=0;
        if (imageMessage.ypos<0)imageMessage.ypos=0;

        for(int x = imageMessage.xpos; x< xmax; x++){
            for(int y = imageMessage.ypos; y < ymax; y++){

                int oriRgb = oriImage.getRGB(x,y);

                if(blockData[x][y] == FLAG){

                    oriImage.setRGB(x,y,FLAG);

                    g2d.setColor(color(oriRgb));
                    g2d.setStroke(new BasicStroke(1f));
                    g2d.fillRect(x-imageMessage.xpos, y-imageMessage.ypos, 1, 1);

                }
            }
        }

        // 释放对象
        g2d.dispose();

        imgMap.put(ORI_IMAGE_KEY,oriImage);
        imgMap.put(CUT_IMAGE_KEY,cutImage);

        return imgMap;
    }
    //获取抠图数据，被扣的像素点将使用FLAG进行标记
    public  int[][] getBlockData(BufferedImage oriImage){

        int height = oriImage.getHeight();
        int width = oriImage.getWidth();
        int[][] blockData =new int[width][height];

        Location[] locations = {Location.UP, Location.LEFT, Location.DOWN, Location.RIGHT};

        //矩形
        //此处还可以优化，进行区域扫描 2020.3.31
        for(int x = 0; x< width; x++){
            for(int y = 0; y < height; y++){

                blockData[x][y] = 0;
                if ( (x > XPOS) && (x < (XPOS+CUT_WIDTH))
                    && (y > YPOS) && (y < (YPOS+CUT_HEIGHT))){
                    blockData[x][y] = FLAG;
                }
            }
        }

        //圆形突出区域
        //突出圆形的原点坐标(x,y)
        int xBulgeCenter=0,yBulgeCenter=0;
        //
        int xConcaveCenter=0,yConcaveCenter=0;

        //位于矩形的哪一边，0123--上下左右
        location = locations[new Random().nextInt(3)];
        if(location == Location.UP){
            //上 凸起
            xBulgeCenter = XPOS +  CUT_WIDTH/2;
            yBulgeCenter = YPOS;
            //左　凹陷
            xConcaveCenter = XPOS ;
            yConcaveCenter = YPOS + CUT_HEIGHT/2;

        }
        else if(location == Location.DOWN){
            //下　凸起
            xBulgeCenter = XPOS +  CUT_WIDTH/2;
            yBulgeCenter = YPOS + CUT_HEIGHT;

            //右　凹陷
            xConcaveCenter = XPOS +  CUT_WIDTH;
            yConcaveCenter = YPOS + CUT_HEIGHT/2;
        }
        else if(location == Location.LEFT){
            //左　凸起
            xBulgeCenter = XPOS ;
            yBulgeCenter = YPOS + CUT_HEIGHT/2;

            //下　凹陷
            xConcaveCenter = XPOS +  CUT_WIDTH/2;
            yConcaveCenter = YPOS + CUT_HEIGHT;

        }
        else {
            //Location.RIGHT
            //右　凸起
            xBulgeCenter = XPOS +  CUT_WIDTH;
            yBulgeCenter = YPOS + CUT_HEIGHT/2;
            //上　凹陷
            xConcaveCenter = XPOS +  CUT_WIDTH/2;
            yConcaveCenter = YPOS;


        }



        //for test
        log.info("突出圆形位置:"+location);

        log.info("XPOS={}  YPOS={}",XPOS,YPOS);
        log.info("xBulgeCenter={}  yBulgeCenter={}",xBulgeCenter,yBulgeCenter);
        log.info("xConcaveCenter={}  yConcaveCenter={}",xConcaveCenter,yConcaveCenter);

        //半径的平方
        int RADIUS_POW2 = RADIUS * RADIUS;

        //凸起部分
        int subX = xBulgeCenter-RADIUS;
        int subY = yBulgeCenter-RADIUS;
        if (subX<0)subX=0;
        if (subY<0)subY=0;
        for(int x = subX; x< xBulgeCenter+RADIUS; x++){
            for(int y = subY; y < yBulgeCenter+RADIUS; y++){
                //(x-a)2+(y-b)2 = r2

                if(Math.pow((x-xBulgeCenter),2) + Math.pow((y-yBulgeCenter),2) < RADIUS_POW2){
                    blockData[x][y] = FLAG;
                }
            }
        }

        //凹陷部分
        for(int x = xConcaveCenter-RADIUS; x< xConcaveCenter+RADIUS; x++){
            for(int y = yConcaveCenter-RADIUS; y < yConcaveCenter+RADIUS; y++){
                //(x-a)2+(y-b)2 = r2

                if(Math.pow((x-xConcaveCenter),2) + Math.pow((y-yConcaveCenter),2) <= RADIUS_POW2){
                    blockData[x][y] = 0;
                }
            }
        }



        return blockData;
    }


    //获取BufferedImage对象
    public  BufferedImage getBufferedImage(File file)throws IOException{

        return ImageIO.read(file);
    }


    //将图片转为base64存储
    private  String ImageBase64(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", out);
        //转成byte数组
        byte[] bytes = out.toByteArray();
        Base64.Encoder encoder = Base64.getEncoder();

        //生成BASE64编码
        return encoder.encodeToString(bytes);
    }

    //随机获取一个图片文件
    private   File getRandomImage(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("该文件路径{}不对", file.getAbsolutePath());
            throw new IOException("该文件路径不对");
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files.length <= 0) {
                throw new IOException("该文件夹内没有文件！");
            } else {

                int index = new Random().nextInt(files.length);
                return files[index];
            }
        } else {
            throw new IOException("该文件夹内没有文件！");
        }
    }

    //压缩图片
    private  BufferedImage compressImage(BufferedImage image,int width,int height) throws IOException{
       return  Thumbnails.of(image)
                .forceSize(width,height)
               //.width(width).height(height)
               .asBufferedImage();
    }
    //抠图部分凸起的区域
    private enum Location {
        UP,
        LEFT,
        DOWN,
        RIGHT;

    }
}
