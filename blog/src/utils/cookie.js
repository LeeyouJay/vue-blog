import Cookies from 'js-cookie'

const TokenKey = 'Arslinth_TokenKey'

const AvatarKey = 'Arslinth_Avatar'

const expireDays = 30

export function getToken() {
	return Cookies.get(TokenKey);
}

export function setToken(token) {
	return Cookies.set(TokenKey, token);
}

export function removeToken() {
	return Cookies.remove(TokenKey);
}

export function removeAvatar() {
	return Cookies.remove(AvatarKey);
}

export function setAvatar(token) {
	return Cookies.set(AvatarKey, token ,{expires:expireDays});
}

export function getAvatar() {
	return Cookies.get(AvatarKey);
}

export function setCookies(key,value) {
	return Cookies.set(key ,value ,{expires:expireDays});
}

export function getCookiesByKey(key) {
	return Cookies.get(key);
}