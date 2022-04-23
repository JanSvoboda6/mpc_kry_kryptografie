import aesjs from "aes-js";

/**
 * Service class for encrypting/decrypting.
 */
const encrypt = (bytes) => {
    const key = aesjs.utils.hex.toBytes(localStorage.getItem("crypto_key"));
    let aesCounter = new aesjs.ModeOfOperation.ctr(key);
    return aesjs.utils.hex.fromBytes(aesCounter.encrypt(bytes));
}

const encryptBytesFormat = (bytes) => {
    const key = aesjs.utils.hex.toBytes(localStorage.getItem("crypto_key"));
    let aesCounter = new aesjs.ModeOfOperation.ctr(key);
    return aesCounter.encrypt(bytes);
}

const decrypt = (bytes) => {
    const key = aesjs.utils.hex.toBytes(localStorage.getItem("crypto_key"));
    let aesCounter = new aesjs.ModeOfOperation.ctr(key);
    return aesjs.utils.hex.fromBytes(aesCounter.decrypt(bytes));
}

const decryptBytesFormat = (bytes) => {
    const key = aesjs.utils.hex.toBytes(localStorage.getItem("crypto_key"));
    let aesCounter = new aesjs.ModeOfOperation.ctr(key);
    return aesCounter.decrypt(bytes);
}


export default { encrypt, decrypt, encryptBytesFormat, decryptBytesFormat }