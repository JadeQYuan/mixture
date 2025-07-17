import CryptoJS from 'crypto-js'

// 密码加密工具方法（反序+两次base64）
export function encryptPassword(password) {
  // 第一步：反序
  let reversed = password.split('').reverse().join('');
  // 第二步：base64加密，去掉末尾=
  let firstBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(reversed)).replace(/=+$/, '');
  // 第三步：再base64加密，去掉末尾=
  let secondBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(firstBase64)).replace(/=+$/, '');
  return secondBase64;
} 