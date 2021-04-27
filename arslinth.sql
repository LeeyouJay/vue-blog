/*
 Navicat Premium Data Transfer

 Source Server         : Arslinth-MySQL
 Source Server Type    : MySQL
 Source Server Version : 80019
 Source Host           : localhost:3306
 Source Schema         : arslinth

 Target Server Type    : MySQL
 Target Server Version : 80019
 File Encoding         : 65001

 Date: 27/04/2021 18:09:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '发布时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '最后一次更新',
  `content` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '文章内容',
  `summary` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '简介',
  `is_top` tinyint(0) NULL DEFAULT 0 COMMENT '是否置顶',
  `title` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
  `views_count` int(0) NULL DEFAULT 0 COMMENT '浏览量',
  `comments_count` int(0) NULL DEFAULT 0 COMMENT '评论数',
  `markdown` tinyint(0) NULL DEFAULT 0 COMMENT '文章类型 true代表Markdown false 代表HTML',
  `banner` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '标图链接',
  `show_type` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '显示类型',
  `tag` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article
-- ----------------------------
INSERT INTO `article` VALUES ('3lsux7gzh5gkflu4', '8agebga2mr2irt3l', '2021-03-20 17:23:30', '2021-04-27 18:05:54', '## SpringBoot项目打包和部署\n\n### 1）项目打包\n\n1. 说明\n\n   本文档针对 Spring Boot 项目来打包和部署！\n\n2. jar 打包\n\n   修改 pom.xml 文件：\n\n   ```xml\n   <!-- 将packaging改为jar -->\n   <packaging>jar</packaging>\n   ```\n\n   ```xml\n   <dependency>\n   	<groupId>org.springframework.boot</groupId>\n   	<artifactId>spring-boot-starter-tomcat</artifactId>\n       <!-- 打包jar需要内嵌tomcat，将以下scope内容注释或删除 -->\n   	<!-- <scope>provided</scope> -->\n   </dependency>\n   ```\n\n3. war 打包\n\n   修改 pom.xml 文件：\n\n   ```xml\n   <!-- 将packaging改为war -->\n   <packaging>war</packaging>\n   ```\n\n   ```xml\n   <dependency>\n   	<groupId>org.springframework.boot</groupId>\n   	<artifactId>spring-boot-starter-tomcat</artifactId>\n       <!-- 将scope改为provided，打包war不需要内嵌tomcat -->\n   	<scope>provided</scope>\n   </dependency>\n   ```\n\n4. 使用 maven 的 `clean package` 命令打包即可\n\n   ```sh\n   mvn clean package\n   ```\n\n   `clean` - 清理项目缓存的输出，在`target`文件夹中的内容将被删除。\n\n   `package` - 打包项目，先`clean`再`package`可以获得最新的部署。\n\n   > 打包成功后，会在项目根目录中的`target`文件夹里生成`appname-0.0.1-SNAPSHOT.war`或`appname-0.0.1-SNAPSHOT.jar`文件，这就是打包后的文件。\n\n\n\n### 2）项目部署（jar）\n\n如果是要部署到云服务器上，将打包好的 .war 或 .jar 文件使用sftp上传到云服务：\n\n```sh\nsftp> put appname-0.0.1-SNAPSHOT.jar\n```\n\n> 建议使用支持sftp的客户端软件直接上传，比如MobaXterm！\n\n执行以下命令来运行：\n\n```powershell\njava -jar appname-0.0.1-SNAPSHOT.jar\n```\n\n上面的运行方式，只要关闭控制台，就中断运行了。使用以下命令可以使其在后台运行，关闭控制台也不受影响：\n\n```powershell\njava -jar appname-0.0.1-SNAPSHOT.jar > appname.log 2>&1 &\n```\n\n对于上面命令的说明：\n\n1. bash 中 0、1、2 三个数字分别代表 STDIN_FILENO 、 STDOUT_FILENO 、STDERR_FILENO ，即标准输入（一般是键盘），标准输出（一般是显示屏，准确的说是用户终端控制台），标准错误（出错信息输出）。\n\n   | 数字 | 含义                                               |\n   | ---- | -------------------------------------------------- |\n   | 0    | 标准输入（一般是键盘）                             |\n   | 1    | 标准输出（一般是显示屏，准确的说是用户终端控制台） |\n   | 2    | 标准错误（出错信息输出）                           |\n\n2. 输入输出可以重定向，所谓重定向输入就是在命令中指定具体的输入来源，譬如 `cat < test.c` 将 test.c 重定向为 cat 命令的输入源。输出重定向是指定具体的输出目标以替换默认的标准输出，譬如 `ls > 1.txt` 将 ls 的结果从标准输出重定向为 1.txt 文本。\n\n   有时候会看到如 `ls >> 1.txt` 这类的写法。\n\n   `>` 和 `>>` 的区别在于：`>` 用于新建而 `>>` 用于追加。即 `ls > 1.txt` 会新建一个 1.txt 文件并且将 ls 的内容输出到新建的 1.txt 中，而 `ls >> 1.txt` 则用在 1.txt 已经存在，而我们只是想将 ls 的内容追加到 1.txt 文本中的时候。\n\n3. 默认输入只有一个（0，STDIN_FILENO），而默认输出有两个（标准输出1 STDOUT_FILENO，标准错误2 STDERR_FILENO）。因此默认情况下，shell 输出的错误信息会被输出到 2，而普通输出信息会输出到 1 。但是某些情况下，我们希望在一个终端下看到所有的信息（包括标准输出信息和错误信息），要怎么办呢？对了，你可以使用我们上面讲到的输出重定向。思路有了，怎么写呢？ 非常直观的想法就是 2>1（将2重定向到1嘛），行不行呢？试一试就知道了。我们进行以下测试步骤：\n\n   | 步骤 |         命令          |                             说明                             |\n   | :--: | :-------------------: | :----------------------------------------------------------: |\n   |  1   | mkdir test && cd test |               创建 test 文件夹并进入 test 目录               |\n   |  2   |   touch a.txt b.c c   |                 创建 a.txt、b.c、c 三个文件                  |\n   |  3   |        ls > 1         | 按我们的猜测，这句应该是将 ls 的结果重定向到标准输出，因此效果和直接 ls 应该一样。但是实际这句执行后，标准输出中并没有任何信息 |\n   |  4   |          ls           |  执行 3 之后再次 ls ，则会看到 test 文件夹中多了一个文件 1   |\n   |  5   |         cat 1         |          查看文件1的内容，实际结果为：1 a.txt b.c c          |\n\n   可见步骤 3 中 `ls > 1` 并不是将 ls 的结果重定向为标准输出，而是将结果重定向到了一个文件 1 中。即 1 在此处不被解释为 STDOUT_FILENO，而是文件1。\n\n4. 到了此时，你应该也能猜到 `2>&1` 的用意了。不错，`2>&1` 就是用来将标准错误 2 重定向到标准输出 1 中的。此处 1 前面的 & 就是为了让 bash 将 1 解释成标准输出而不是文件 1 。至于最后一个 & ，则是让 bash 在后台执行。\n\n这个时候，如果想关掉后台运行的进程服务，则需要这样做：\n\n```powershell\n# 查看java进程的状态，主要看进程id\nps aux | grep java\n# 根据进程id来结束后台进程\nkill -9 [进程id]\n```\n\n\n\n### 3）项目部署（war）\n\n1. **方式一：**\n\n   将`appname-0.0.1-SNAPSHOT.war`项目打包文件，放到`tomcat`的`webapps`目录中，然后启动`tomcat`即可。\n\n   > 这种方式操作简单，但是不能将项目的上下文路径设为`/`，因为`tomcat`启动时将`appname-0.0.1-SNAPSHOT.war`归档文件释放为`appname-0.0.1-SNAPSHOT`文件夹，所以我们在访问时，要这么访问：\n   >\n   > http://localhost:8080**/appname-0.0.1-SNAPSHOT**/web/login.html\n\n2. **方式二：**\n\n   修改`tomcat`的`conf/server.xml`文件，在`<Host>`标签中，添加以下内容：\n\n   ```xml\n   <Context docBase=\"[war文件路径]\" path=\"[上下文路径]\" reloadable=\"true\" />\n   ```\n\n   示例：\n\n   ```xml\n   <Host name=\"localhost\"  appBase=\"webapps\"\n         unpackWARs=\"true\" autoDeploy=\"true\">\n   \n       <!-- SingleSignOn valve, share authentication between web applications\n                Documentation at: /docs/config/valve.html -->\n       <!--\n           <Valve className=\"org.apache.catalina.authenticator.SingleSignOn\" />\n           -->\n   \n       <!-- Access log processes all example.\n                Documentation at: /docs/config/valve.html\n                Note: The pattern used is equivalent to using pattern=\"common\" -->\n       <Valve className=\"org.apache.catalina.valves.AccessLogValve\" directory=\"logs\"\n              prefix=\"localhost_access_log\" suffix=\".txt\"\n              pattern=\"%h %l %u %t &quot;%r&quot; %s %b\" />\n   \n       <!-- 添加以下内容即可 -->\n       <Context docBase=\"/root/appname-0.0.1-SNAPSHOT.war\" path=\"/\" reloadable=\"true\" />\n   \n   </Host>\n   ```\n\n   当我们将`<Context>`标签的`path`属性设为`\"/\"`或`\"\"`时，那么我们就可以这么访问：\n\n   http://localhost:8080/web/login.html\n\n   不需要加`/appname-0.0.1-SNAPSHOT`上下文路径了\n\n', 'PostBy Liangshishen', 0, 'SpringBoot项目打包和部署', 2, 0, 1, 'https://get.pxhere.com/photo/sun-sky-nature-atmospheric-phenomenon-tree-cloud-sunset-grass-sunlight-morning-light-sunrise-reflection-plant-field-dawn-evening-leaf-grass-family-autumn-mist-flower-dusk-99190.jpg', '公开', 'SpringBoot');
INSERT INTO `article` VALUES ('tpdcb60dbb71qp25', '8agebga2mr2irt3l', '2021-03-24 10:25:01', '2021-04-27 18:04:45', '<p><span style=\"font-weight: normal;\"></span>&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style=\"font-size: 1em;\">软实力是近年来风靡国际关系领域的最流行关键词，它深刻地影响了人们对国际关系的看法，使人们从关心领土、军备、武力、科技进步、经济发展、地域扩张、军事打击等有形的</span><span style=\"font-size: 1em;\">“硬实力，转向关注文化、价值观、影响力、道德准则、文化感召力等无形的“软实力”。</span><span style=\"font-size: 1em;\">一个国家或民族若想提高自身的国际地位和国际影响力，不仅仅需要提高自身的硬实力，更应提高、宣传自身的文化软实力。随着经济的快速发展</span><span style=\"font-size: 1em;\">,中国国家地位和国际影响力日益提高，家硬实力获得大幅提升，家软实力也得到长足发展,但也同时面临着“中国威胁论”“中国崩溃论”“中国责任论”等话题带来的国际舆论担忧。因此，提升文化软实力成为新时代背景下增强国家影响力和形象的重要举措。</span></p><h2 id=\"jogl5\">一、文化软实力的概念及作用</h2><p>&nbsp; &nbsp; &nbsp; &nbsp;软实力是近年来风靡国际关系领域的最流行关键词，它深刻地影响了人们对国际关系的看法，使人们从关心领土、军备、武力、科技进步、经济发展、地域扩张、军事打击等有形的“硬实力”，向关注文化、价值观、影响力、道德准则、文化感召力等无形的“软实力”。“软实力”的概念是由<a href=\"https://baike.sogou.com/lemma/ShowInnerLink.htm?lemmaId=63134&amp;ss_c=ssc.citiao.link\" target=\"_blank\">美国哈佛大学</a>教授小约瑟夫·奈提出来的。约瑟夫·奈指出，一个国家的综合国力既包括由经济、科技、军事实力等表现出来的“硬实力”，也包括以文化和意识形态吸引力体现出来的“软实力”。中国人民大学新闻学院教授喻国明指出：“一个国家是存在两种实力的，一种是硬实力，一种是软实力。硬实力通常是指国家的GDP、硬件设施等，而文化、制度、传媒等被称为软实力。”一个国家或民族若想提高自身的国际地位和国际影响力，不仅仅需要提高自身的硬实力，更应提高、宣传自身的文化软实力。而意识形态是抽象的，中国作为社会主义国家，其意识形态与世界上大多数国家的资本主义意识形态截然不同，这造成了若想提高国家影响力，从国家制度、国家意识形态上让外国公民接受和理解是比较困难的。而文化是一种社会现象，是人们长期创造形成的产物，同时又是一种历史现象，是社会历史的积淀物，是能够被传承的国家或民族的历史、地理、风土人情、传统习俗、生活方式、文学艺术、行为规范、思维方式、价值观念等，是人类之间进行交流的普遍认可的一种能够传承的意识形态。文化交流作为一种国际间交流的重要手段，甚至是最主要的民间交流手段，可以极大程度的向国际输出我国的传统文化、习俗、文学、艺术、价值观、民族精神等。文化软实力集中体现了一个国家的凝聚力和生命力，进而产生国际吸引力和感召力,是加强对外宣传、提升国际认同最低耗、最有效的一种手段。［1］文化软实力可以通过深邃的文化内涵和饱满的精神状态来塑造良好的国家文化形象；树立包容、创新的国家文化形象,有助于提升和坚定一个民族的文化自信。因此，探索二者的逻辑关系,筑牢文化自信的根基,彰显文明、包容、创新于一体的国家文化形象,对增强国家软实力和实现中华民族复兴中国梦意义重大。[2]</p><h2 id=\"5hjr9\">二、提升文化软实力的必要性</h2><p>（一）我国国家形象不断受外国资本主义抹黑，需要通过文化软实力来宣传国家形象</p><p>&nbsp; &nbsp; &nbsp; &nbsp;十九大明确了中国特色社会主义已进入新时代,在新时代文化软实力已经成为综合国力竞争的重要因素,谁拥有了强大的文化软实力，谁就能在国际竞争中处于主动地位。提高国家软实力是一项国家战略。我国改革开放以来,随着经济的快速发展，中国国家地位和国际影响力日益提高,国家硬实力获得大幅提升，国家软实力也得到长足发展,但也同时面临着“中国威胁论”、“中国崩溃论”、“中国责任论”等话题带来的国际舆论担忧；国际社会对中国肩负责任期望等问题的困扰和挑战，在客观上存在软实力发展缓慢和不平衡等问题。如何明确我国在世界文化格局中的定位，提升国家软实力,是当前面临的一个重大现实课题。坚定文化自信，树立良好国家文化形象，是文化软实力提升的重要表现和最终旨归。如果说“硬实力”是外交时坚强的武器，那么，“文化外交”则起到了“随风潜入夜、润物细无声”的作用。</p><p>（二）、文化是提升国家形象的重要的外交手段</p><p>&nbsp; &nbsp; &nbsp; &nbsp;在国际交往中文化外交是传播国家或民族文化的重要途径，也是实现国家外交战略目标的有效手段，对于国家形象塑造具有重要意义。[3]因而，许多国家都将文化外交作为国家外交活动的重要策略，通过文化传播、文化沟通等方式传播本国的民族传统文化，力求将本国文化转化为国际主流文化。比如许多西方国家都通过文化交流、文化谈判、缔结文化条约等方式推进文化外交活动，展现本国的优秀文化形象，提高本国本民族的文化竞争力。同时，许多国家都通过广播、电视、互联网等开展文化传播活动，化解本国在国际社会上的负面印象，提高本国的国际影响力。[4]我国由于综合国力的不断提升机国际话语权的增大，饱受部分国家及媒体的“中国威胁论”、“中国崩溃论”等话题带来的国际舆论困扰，运用文化外交手段消除“中国威胁论”带来的种种影响，可以更好地推进中国的国家形象建构。[5]</p><p>（三）提高文化软实力有助于加强自身的文化自信</p><p>&nbsp; &nbsp; &nbsp; &nbsp;随着经济全球化的发展，文化也趋向于全球化，改革开放以来，我国不断学习发达国家经济建设经验，取得了举世瞩目的经济建筑成绩。但与此同时，随着全球化发展，大量西方文化进入中国市场，强烈冲击了我国传统文化，甚至青少年群体中出现了中国人流行过“感恩节、圣诞节”等洋节日而对中国传统节日缺乏热情、“外国的月亮比较圆”的思想潮流。西方文化渗透的基本类型主要有西方资本主义对社会主义国家的文化渗透、西方发达国家对发展中国家的文化渗透，美国对西方发达国家的文化渗透,不同类型的文化渗透针对不同的国家和地区,在渗透过程中采取的措施也存在一定的差异。无论在哪一种文化渗透类型中,其渗透内容都包括：为日常生活模式、消费产品、意识形态载体的表层物质文化；以西方民主制度、选举制度和人权制度为主的中层制度文化；以及以个人主义、自由主义、消费主义等价值观为代表的深层文化。从物质文化的强势侵袭，到制度文化的移植,最后到价值观的变革,是一个循序渐递的过程。为了完成政治、经济入侵，西方文化渗透的途径蒙上了一层温情的面纱，现得极为隐蔽,比较典型的途径有：经济扩张途径,网络信息技术传播途径，英代理途径，宗教渗透途径等。[6]提升文化软实力，有利于坚定我们民族自身的文化自信，有助于加强青少年群体对于自身优秀文化的认识的加深，进而加深对于我们自身文化的认同，更进一步认识和接纳西方文化、政治意识形态与本国的文化、政治意识形态的区别，增强他们对于国家、民族的认同感、自豪感、归属感。</p><h2 id=\"nt2ld\">三、用多元途径提升文化软实力</h2><h3 id=\"m3dmo\">（一）推进文化事业和文化产业的发展</h3><p>1.文化事业普惠性发展，提升人民群众文化获得感。</p><p>&nbsp; &nbsp; &nbsp; &nbsp;首先，促进公共文化服务设施均衡化是推进文化事业繁荣发展的关键。只有让广大人民群众广泛地享有公共文化服务的权利，极大地丰富人民群众的文化生活，人民群众才能够自觉地参与到提升国家文化软实力的行动当中去。当前，我国仍然存在城乡发展不均衡的状况，农村文化事业的发展滞后一定程度上阻碍了整个文化事业的发展进程，所以完善公共文化服务体系的当务之急就要重视加强农村公共文化服务设施建设，促进农村文化事业的发展，加大对公益性文化服务设施的资金投入量。</p><p>2.扩大文化产品覆盖范围是推动文化产业健康发展的重点。</p><p>&nbsp; &nbsp; &nbsp; &nbsp;提高国家文化软实力就需要提升民族精神建设，需要扩大具有本民族特色的文化产品的范围。外来文化之所以能够占有我国市场的一部分，其原因在于善于抓住群众的娱乐特征进行文化产品内容及形式的创新。因此提升国家文化软实力要扩大本民族文化产品的覆盖范围，与时俱进，生产出具有中国本民族特色的文化产品。提升国家文化软实力还应当将带着本民族特色的文化产品走出国门、\n走向世界。例如，2017年以爱国主义为题材的国产电影《战狼2》在汲取好莱坞电影制作优点的同时注入了新的血液，“创造出丰富多样的中国故事、中国形象、中国旋律，为世界贡献特殊的声响和色彩、展现特殊的诗情和意境”。[7]</p><p>&nbsp;</p><h3 id=\"kitj9\">（二）大力弘扬中国优秀传统文化、让传统文化披上新外衣</h3><p>1.大力宣传传统节日、传统习俗。</p><p>&nbsp; &nbsp; &nbsp; &nbsp;传统节日和习俗是中华传统文化的重要组成部分，它既是海内外中华儿女世代相传的共同记忆，也是中华民族的一种社会生活方式，它绵延数千年，传递着中华民族一贯秉持的尊老爱幼、夫妻和睦、勤俭持家、邻里团结、和而不同、求同存异等传统美德和价值理念，成为中华民族的基因，植根在中国人内心，潜移默化影响着中国人的思想方式和行为方式。[8]</p><p>近年来，央视等媒体在各个传统节日时大量报导不同地域、不同节日的民俗风情，以及开创“如果国宝会说话”、“舌尖上的中国”、“上新了故宫”等优秀节目等，这是传统节日可以不断挖掘的“文化富矿”，同时也是弘扬自身文化的非常重要的途径。新闻媒体对传统节日的集中报道和广泛传播，可以有效激发人们对国家富强、人民幸福所展现出来的理想追求，用内在的力量和自信，外化为现实生活的动力，推动中华民族最基本的文化基因与当代文化相适应、与现代社会相协调。</p><p>2.用创新和科技，给传统文化注入新的生命力。</p><p>&nbsp; &nbsp; &nbsp; &nbsp;创新是文化保持活力的源泉，是文化的应有之义，文化创新力的高低同时关系文化生产力的高低，创新能吸引消费者的眼球，增加产品的附加价值和市场有率。以故宫博物院为例，在2015故宫文化创意馆”，截止2016年年底，故宫的文化产品达到约9170年，文化产品销售额达15右；2018博物院的微博粉丝数量约600化软实力成为推动故宫博物院文化产业发展的主要动因，故宫博物院在保护文物的基础上，通过文化产品的形式深挖文化资源的思想内涵，立足社会现实需求，兼顾实用性和时尚性，创造性地将文化资源优势转换化成产业发展优势。故宫博物院文创旗舰店的畅销品如祥瑞主题的帆布包、手机壳、文件夹，太平有象主题的挂饰、书签，宫廷团扇等，这些文化产品的研发基于故宫博物院的馆藏，寓意吉祥，具有基本的使用价值和较高的审美价值；古风古韵，具有鲜明的艺术风格和表现形式；别具匠心，兼具实用性和趣味性。文化产业是一个文化生产力的具体体现过程，文化生产力的核心是文化创新力，文化创新力在传统文化和现代消费之间搭建桥梁。故宫博物院文化产业采用年轻人喜闻乐见的方式把传统非遗打造成\n时尚爆款，文化产业随着文化创新力而发展。[9]</p><h3 id=\"i7ezr\">（三）增加传统文化输出，加强国际间交流</h3><p>1.让世界更多遍布中国话。</p><p>&nbsp; &nbsp; &nbsp; &nbsp;自2004年首家孔子学院在韩国首尔挂牌，截\n至 2013年9月，全球已建立435所孔子学院和644个孔子课堂，分布在117个国家和地区，而且《孔子学院规划2012—2020》还明确指出，到2015年，全球要达到 500所孔子学院，中小学孔子课堂达到1000个，学员达到 150万人。[10]以孔子为代表的儒学文化，对中华民族的影响极其深远，其倡导的“仁、义、礼、智、信”构成了了中华优秀文化的重要组成部分。作为最早的中国文化传播媒介，各国孔子学院的创立及发展壮大，向世界展示了中国声音，其核心文化也向世界人民宣示了诸如“中国威胁论”这样的言论的不切实际。</p><p><span style=\"font-weight: normal;\">2.增加国际间文艺交流演出，让“国粹”走出国门。</span></p><p>2009年7月，时任国家主席胡锦涛在第十一次驻外使节会议上特别强调：“要加强公共外交和人文外交，开展多种形式的对外文化交流活动，扎实传播中华优秀文化。随着国际间经济交流、旅游愈加频繁，我国许多优秀传统文化也迎来新的发展契机，诸如京剧、川剧、梅花剧等各个传统艺术剧团不断走出国门，向世界展示中国风采。</p><p>3.“一带一路”为中国传统文化的对外交流带来的机遇。</p><p>&nbsp; &nbsp; &nbsp; &nbsp;丝绸之路自古以来就承担着我国传统文化传播的重任。我国与沿线国家特别是亚欧非国家之间进行的合作交流自实\n施“一带一路”以来有了更多的便利和机会。“一带一路”倡议给中国传统文化的对外交流带来了巨大的机遇，并充分发挥了自身的桥梁作用。让中国传统文化走出国门是我国在改革开\n放之后所一直提倡的。而世界文化的大发展、大融合在当下全球化不断深入的趋势下也是未来文化发展的必然要求。为了让我国优秀的传统文化更好地推动世界文化的发展，在“一带一路”背景下，更传播并大力发展我国优秀的传统文化。[11]</p><p>总之，发展社会主义先进文化推动中华民族伟大复兴中国梦征途中，中国文化必将放射出璀璨的光芒。我们一定会形成与我国经济基础，人民需求，国家地位相匹配的文化优势，增强国家文化软实力，把我国建设成为一个社会主义文化强国。</p><p>参考文献</p><p><!--[if-->[1].<!--[endif]-->[1]江薇薇. 提升中国软实力的文化叙事五维策略[D].华中科技大学,2019.</p><p><!--[if-->[2].<!--[endif]-->[2]王福生. 新时代中国文化软实力的价值传播[N]. 中国社会科学报,2019-01-31(001).</p><p><!--[if-->[3].<!--[endif]-->[3][4][5]刘鑫.以文化外交推进中国国家形象建构[J].辽宁行政学院学报,2018(02):24-26.</p><p><!--[if-->[4].<!--[endif]-->[6]姚金艳. 全球化时代西方文化渗透研究[D].华中科技大学,2018.</p><p><!--[if-->[5].<!--[endif]-->[7]尹璐璐.提升国家文化软实力的路径探析[J].学理论,2019(06):16-17+23.</p><p><!--[if-->[6].<!--[endif]-->[8]刘斌.文化传承与家国情怀——以央视的传统节日报道为例[J].青年记者,2018(33):19-21.</p><p><!--[if-->[7].<!--[endif]-->[9]洪晓楠,周爱玲.文化软实力视野下的我国文化产业发展路径研究——以故宫博物院文化产业发展为例[J].文化软实力,2019,4(03):76-81.</p><p><!--[if-->[8].<!--[endif]-->[10]孔子学院总部暨国家汉语国际推广领导小组办公室.年度报告 [R]. 内部资料，2013：5 .</p><p><!--[if-->[9].<!--[endif]-->[11]肖月,匡丽.一带一路背景下中国传统文化的对外交流途径[J].湖北开放职业学院学报,2019,32(18):113-114.</p>', '软实力是近年来风靡国际关系领域的最流行关键词，它深刻地影响了人们对国际关系的看法，使人们从关心领土、军备、武力、科技进步、经济发展、地域扩张、军事打击等有形的“硬实力，转向关注文化、价值观、影响力、道', 1, '新时代背景下提升我国文化软实力的重要性及路径', 6, 0, 0, 'https://c.pxhere.com/photos/0d/d2/redwood_national_park_california_hdr_landscape_scenic_dawn_daybreak_sunrise-548750.jpg!d', '公开', '随笔');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `article_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文章id',
  `from_user_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评论者id(匿名则无)',
  `from_user_email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评论者邮箱',
  `from_user_avatar` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评论头像',
  `from_user_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评论昵称',
  `to_user_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `to_user_email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `to_user_avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `to_user_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `parent_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上级评论id',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '内容',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `from_user_site` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '网站',
  `browser` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '浏览器',
  `system_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统名称',
  `has_new` tinyint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES ('m0osc1syyxtvzrgc', 'about', '8agebga2mr2irt3l', '752279593@qq.com', '/loadImg/admin-thumbnail.jpg', 'Arslinth', NULL, NULL, NULL, NULL, NULL, '<img data-v-2a0cec31=\"\" width=\"20\" height=\"20\" src=\"/img/1f603.66f6c781.svg\" alt=\"\" class=\"emoji-icon\" style=\"vertical-align: text-top;\">那我就先来第一条留言吧~~', '2021-04-23 02:21:21', '', 'Chrome 9', 'Windows', 0);

-- ----------------------------
-- Table structure for friend
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend`  (
  `id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `site_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `site_link` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `icon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `has_check` tinyint(0) NULL DEFAULT 0,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role_auths
-- ----------------------------
DROP TABLE IF EXISTS `role_auths`;
CREATE TABLE `role_auths`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `role_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `auth` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 741 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_auths
-- ----------------------------
INSERT INTO `role_auths` VALUES (710, '8gzsu99ll6tqsiql', 'blog');
INSERT INTO `role_auths` VALUES (711, '8gzsu99ll6tqsiql', 'write_html');
INSERT INTO `role_auths` VALUES (712, '8gzsu99ll6tqsiql', 'ht_publish');
INSERT INTO `role_auths` VALUES (713, '8gzsu99ll6tqsiql', 'article_list');
INSERT INTO `role_auths` VALUES (714, '8gzsu99ll6tqsiql', 'article_del');
INSERT INTO `role_auths` VALUES (715, '8gzsu99ll6tqsiql', 'write_markdown');
INSERT INTO `role_auths` VALUES (716, '8gzsu99ll6tqsiql', 'md_publish');
INSERT INTO `role_auths` VALUES (717, '8gzsu99ll6tqsiql', 'dashboard');
INSERT INTO `role_auths` VALUES (718, 'dxqkeiizg9qdxmcy', 'system');
INSERT INTO `role_auths` VALUES (719, 'dxqkeiizg9qdxmcy', 'user_list');
INSERT INTO `role_auths` VALUES (720, 'dxqkeiizg9qdxmcy', 'setAuthority');
INSERT INTO `role_auths` VALUES (721, 'dxqkeiizg9qdxmcy', 'resetPassword');
INSERT INTO `role_auths` VALUES (722, 'dxqkeiizg9qdxmcy', 'changState');
INSERT INTO `role_auths` VALUES (723, 'dxqkeiizg9qdxmcy', 'sysRoles');
INSERT INTO `role_auths` VALUES (724, 'dxqkeiizg9qdxmcy', 'add_sysRole');
INSERT INTO `role_auths` VALUES (725, 'dxqkeiizg9qdxmcy', 'delete_sysRole');
INSERT INTO `role_auths` VALUES (726, 'dxqkeiizg9qdxmcy', 'set_roleAuths');
INSERT INTO `role_auths` VALUES (727, 'dxqkeiizg9qdxmcy', 'change_userAuths');
INSERT INTO `role_auths` VALUES (728, 'dxqkeiizg9qdxmcy', 'menu');
INSERT INTO `role_auths` VALUES (729, 'dxqkeiizg9qdxmcy', 'delMenu');
INSERT INTO `role_auths` VALUES (730, 'dxqkeiizg9qdxmcy', 'editMenu');
INSERT INTO `role_auths` VALUES (731, 'dxqkeiizg9qdxmcy', 'addMenu');
INSERT INTO `role_auths` VALUES (732, 'dxqkeiizg9qdxmcy', 'sysLog');
INSERT INTO `role_auths` VALUES (733, 'dxqkeiizg9qdxmcy', 'blog');
INSERT INTO `role_auths` VALUES (734, 'dxqkeiizg9qdxmcy', 'write_html');
INSERT INTO `role_auths` VALUES (735, 'dxqkeiizg9qdxmcy', 'ht_publish');
INSERT INTO `role_auths` VALUES (736, 'dxqkeiizg9qdxmcy', 'article_list');
INSERT INTO `role_auths` VALUES (737, 'dxqkeiizg9qdxmcy', 'article_del');
INSERT INTO `role_auths` VALUES (738, 'dxqkeiizg9qdxmcy', 'write_markdown');
INSERT INTO `role_auths` VALUES (739, 'dxqkeiizg9qdxmcy', 'md_publish');
INSERT INTO `role_auths` VALUES (740, 'dxqkeiizg9qdxmcy', 'friend_list');
INSERT INTO `role_auths` VALUES (741, 'dxqkeiizg9qdxmcy', 'dashboard');

-- ----------------------------
-- Table structure for sys_authority
-- ----------------------------
DROP TABLE IF EXISTS `sys_authority`;
CREATE TABLE `sys_authority`  (
  `id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `parent_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'none' COMMENT '上级id',
  `authority` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限代码',
  `authority_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限名称',
  `authority_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限类型',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `level` tinyint(0) NULL DEFAULT 0 COMMENT '层级',
  `sorted` tinyint(0) NULL DEFAULT NULL COMMENT '排序',
  `icon` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_authority
-- ----------------------------
INSERT INTO `sys_authority` VALUES ('0cmcm9tmbqz1uhbv', '4s4vpmggzlo82wcy', 'setAuthority', '设置权限', '按钮', '2021-03-05 04:02:31', 2, 0, '');
INSERT INTO `sys_authority` VALUES ('1iw8jtasb4agzi79', '4s4vpmggzlo82wcy', 'changState', '更改状态', '按钮', '2021-03-05 04:30:19', 2, 2, '');
INSERT INTO `sys_authority` VALUES ('4s4vpmggzlo82wcy', '99486uc1ixdticwz', 'user_list', '用户列表', '页面', '2021-02-27 13:25:38', 1, 0, 'el-icon-user');
INSERT INTO `sys_authority` VALUES ('4sgu9bk8bjeqr312', '8h5t3urmy9wbdenc', 'friend_list', '友链申请', '页面', '2021-04-21 22:43:53', 1, 3, 'el-icon-s-check');
INSERT INTO `sys_authority` VALUES ('6moqvkeo2kne1lo8', '99486uc1ixdticwz', 'menu', '菜单管理', '页面', '2021-02-27 13:26:19', 1, 2, 'el-icon-document-checked');
INSERT INTO `sys_authority` VALUES ('70phyi1gr9efdf78', 'a2h9qho4y4powi1p', 'article_del', '删除', '按钮', '2021-03-22 21:46:16', 2, 0, '');
INSERT INTO `sys_authority` VALUES ('8h5t3urmy9wbdenc', 'none', 'blog', '博客管理', '页面', '2021-03-19 09:53:56', 0, 1, 'el-icon-c-scale-to-original');
INSERT INTO `sys_authority` VALUES ('99486uc1ixdticwz', 'none', 'system', '系统设置', '页面', '2021-02-27 13:23:56', 0, 0, 'el-icon-setting');
INSERT INTO `sys_authority` VALUES ('a2h9qho4y4powi1p', '8h5t3urmy9wbdenc', 'article_list', '文章列表', '页面', '2021-03-19 23:15:48', 1, 1, 'el-icon-document-copy');
INSERT INTO `sys_authority` VALUES ('d952ucz1kcd8hmkb', 'f3ktsapk0te0ga5j', 'add_sysRole', '添加角色', '按钮', '2021-03-08 11:04:07', 2, 0, '');
INSERT INTO `sys_authority` VALUES ('dgexj10vqatvlluk', '99486uc1ixdticwz', 'sysLog', '登入日志', '页面', '2021-03-04 18:15:13', 1, 3, 'el-icon-edit-outline');
INSERT INTO `sys_authority` VALUES ('doent6ao4w35hjdb', '6moqvkeo2kne1lo8', 'editMenu', '修改菜单', '按钮', '2021-03-05 03:48:19', 2, 1, '');
INSERT INTO `sys_authority` VALUES ('ekfeeplq2h1e6plj', 'f3ktsapk0te0ga5j', 'set_roleAuths', '修改角色权限', '按钮', '2021-03-08 11:05:23', 2, 2, '');
INSERT INTO `sys_authority` VALUES ('f3ktsapk0te0ga5j', '99486uc1ixdticwz', 'sysRoles', '角色管理', '页面', '2021-03-07 10:12:13', 1, 1, 'el-icon-user-solid');
INSERT INTO `sys_authority` VALUES ('fecehghxmj4tsccq', '8h5t3urmy9wbdenc', 'write_markdown', 'Markdown格式', '页面', '2021-03-19 09:55:43', 1, 1, 'el-icon-edit-outline');
INSERT INTO `sys_authority` VALUES ('kgc4hrjai160x6m6', '8h5t3urmy9wbdenc', 'write_html', 'HTML格式', '页面', '2021-03-20 17:41:38', 1, 0, 'el-icon-edit');
INSERT INTO `sys_authority` VALUES ('s2b3bemjmljfkrk3', '4s4vpmggzlo82wcy', 'resetPassword', '重置密码', '按钮', '2021-03-05 04:02:50', 2, 1, '');
INSERT INTO `sys_authority` VALUES ('uglaz97s6btek0jz', 'f3ktsapk0te0ga5j', 'change_userAuths', '移动用户权限', '按钮', '2021-03-08 11:06:07', 2, 3, '');
INSERT INTO `sys_authority` VALUES ('v0l2mbvap9e2o42q', '6moqvkeo2kne1lo8', 'delMenu', '删除菜单', '按钮', '2021-03-05 03:48:37', 2, 0, '');
INSERT INTO `sys_authority` VALUES ('vinvpj071h2zoxvs', '6moqvkeo2kne1lo8', 'addMenu', '新增菜单', '按钮', '2021-03-05 03:46:53', 2, 2, '');
INSERT INTO `sys_authority` VALUES ('vt5z5mxucy8amhmu', 'f3ktsapk0te0ga5j', 'delete_sysRole', '删除角色', '按钮', '2021-03-08 11:04:43', 2, 1, '');
INSERT INTO `sys_authority` VALUES ('x04g03t6jycwt126', 'fecehghxmj4tsccq', 'md_publish', '发布', '按钮', '2021-03-19 19:02:42', 2, 0, '');
INSERT INTO `sys_authority` VALUES ('xchl782iihfhlygz', 'kgc4hrjai160x6m6', 'ht_publish', '发布', '按钮', '2021-03-22 21:43:58', 2, 0, '');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
  `username` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登入方式',
  `ip_address` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'IP地址',
  `ip_source` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'IP来源',
  `message` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '日志信息',
  `browser_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '浏览器名称',
  `system_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统名称',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `is_success` tinyint(0) NULL DEFAULT NULL COMMENT '是否登入成功判断',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES ('3caagbe0nobe8r3v', 'admin', '账号密码验证', '182.88.232.115', '中国|广西|南宁市|联通', '登录成功！', 'Chrome 9', 'Windows', '2021-04-27 18:03:15', 1);
INSERT INTO `sys_log` VALUES ('3typqjas3h7ro48g', 'admin', '验证码方式', '182.88.232.115', '中国|广西|南宁市|联通', '登录成功！', 'Chrome 9', 'Windows', '2021-04-27 14:38:19', 1);
INSERT INTO `sys_log` VALUES ('akgg6tjt4axxbeel', 'levi', '账号密码验证', '182.88.232.115', '中国|广西|南宁市|联通', '账号不存在！', 'Chrome 9', 'Windows', '2021-04-27 14:16:49', 0);
INSERT INTO `sys_log` VALUES ('c70vn2muv48bw8wb', 'admin', '账号密码验证', '171.37.183.199', '中国|广西|南宁市|联通', '登录成功！', 'Chrome 9', 'Windows', '2021-04-25 15:25:08', 1);
INSERT INTO `sys_log` VALUES ('eaxlgd3quqouqa1z', 'live', '账号密码验证', '182.88.232.115', '中国|广西|南宁市|联通', '登录成功！', 'Chrome 9', 'Windows', '2021-04-27 14:16:58', 1);
INSERT INTO `sys_log` VALUES ('g2riwicewrwsxwj6', 'admin', '账号密码验证', '171.37.183.199', '中国|广西|南宁市|联通', '登录成功！', 'Chrome 9', 'Windows', '2021-04-25 14:58:48', 1);
INSERT INTO `sys_log` VALUES ('jfpci3py2dx4lta0', 'admin', '账号密码验证', '127.0.0.1', '内网IP', '登录成功！', 'Chrome 9', 'Windows', '2021-04-25 01:48:49', 1);
INSERT INTO `sys_log` VALUES ('vtlmp87zmrwjz738', 'admin', '账号密码验证', '182.88.232.115', '中国|广西|南宁市|联通', '登录成功！', 'Chrome 9', 'Windows', '2021-04-27 13:14:59', 1);
INSERT INTO `sys_log` VALUES ('wotgdpo39epnah9r', 'admin', '账号密码验证', '127.0.0.1', '内网IP', '登录成功！', 'Chrome 9', 'Windows', '2021-04-25 01:48:15', 1);
INSERT INTO `sys_log` VALUES ('wvru8h69rn28ac66', 'admin', '账号密码验证', '171.37.183.199', '中国|广西|南宁市|联通', '登录成功！', 'Chrome 9', 'Windows', '2021-04-25 15:35:00', 1);
INSERT INTO `sys_log` VALUES ('xd28drjxn7a77wr0', 'admin', '账号密码验证', '127.0.0.1', '内网IP', '登录成功！', 'Chrome 9', 'Windows', '2021-04-25 14:28:23', 1);
INSERT INTO `sys_log` VALUES ('zu93nov6w66xs7yq', 'admin', '账号密码验证', '127.0.0.1', '内网IP', '登录成功！', 'Chrome 9', 'Windows', '2021-04-25 14:30:08', 1);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `role_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名称',
  `role` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色标识',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('8gzsu99ll6tqsiql', '普通会员', 'normal', '2021-03-07 10:30:46');
INSERT INTO `sys_role` VALUES ('dxqkeiizg9qdxmcy', '超级管理员', 'supper_admin', '2021-03-07 10:29:11');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `role_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联角色id',
  `nick_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `username` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登入账号',
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `avatar` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '保密' COMMENT '性别',
  `state` tinyint(0) NULL DEFAULT NULL COMMENT '账号是否禁用',
  `set_right` tinyint(0) NULL DEFAULT 0 COMMENT '是否已独立设置权限',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('8agebga2mr2irt3l', 'dxqkeiizg9qdxmcy', 'Arslinth', '18677952830', '752279593@qq.com', 'admin', '$2a$10$e3630tO2GKa7AgXv/XAveOMJ0RNxlhSJ49UPUgnOvFyFefze0lr0S', '/loadImg/admin-thumbnail.jpg', '2021-02-24 01:58:33', '保密', 1, 0);
INSERT INTO `sys_user` VALUES ('umvssn2wfuoits1d', 'non', 'leeyou', '18487079121', '1273859150@qq.com', 'leeyou', '$2a$10$j5RvNvJxM7jSYiyoza0VF.Oe9VxD6pHX2Voc92SK3MLCmQXjrNwyK', '/loadImg/leeyou-thumbnail.jpg', '2021-03-19 19:07:23', '保密', 1, 0);

SET FOREIGN_KEY_CHECKS = 1;
