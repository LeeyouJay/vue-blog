import Cookies from 'js-cookie'

const TokenKey = 'Arslinth_TokenKey'

const AvatarKey = 'Arslinth_Avatar'

export function getToken() {
	return Cookies.get(TokenKey);
}

export function setToken(token) {
	return Cookies.set(TokenKey, token);
}

export function removeToken() {
	return Cookies.remove(TokenKey);
}
export function setAvatar(token) {
	return Cookies.set(AvatarKey, token);
}
export function getAvatar() {
	return Cookies.get(AvatarKey);
}
