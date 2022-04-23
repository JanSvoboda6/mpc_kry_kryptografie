import React, {useState} from "react";
import aesjs from "aes-js";
import {Link, useHistory} from 'react-router-dom';
import HelperBox from "../navigation/HelperBox";
import copyIcon from '../../styles/copy_icon.svg';

const Crypto = () =>{
    const history = useHistory();
    let [cryptoKey, setCryptoKey] = useState("");
    let [warningText, setWarningText] = useState("");

    const doGenerateKey = () => {
        const bytes = new Uint8Array(32);
        window.crypto.getRandomValues(bytes);
        return aesjs.utils.hex.fromBytes(bytes);
    }

    const generateKey = () => {
        setWarningText("Please copy the key to your local storage and keep it safe.")
        let key = doGenerateKey();
        localStorage.setItem("crypto_key", key);
        setCryptoKey(key);
    }

    const onChangeCryptoKey = (e: { target: { value: any; }; }) =>
    {
        setWarningText("");
        let key = e.target.value
        localStorage.setItem("crypto_key", key);
        setCryptoKey(key);
    }

    const redirectToFiles = () => {
        history.push('files');
    }

    return(
        <div>
            <div className="wrapper">
                <HelperBox content={"Dear user, please paste your secret key below or if this is your first time here, generate a new one."} onClose={() => null}/>
            </div>
            <div className="crypto-wrapper">
                {warningText && <div className="warning-test">{warningText}</div>}
                <div className="crypto-item">
                    <input
                        type="text"
                        className="input-text-wide crypto-item"
                        name="cryptoKey"
                        placeholder="Paste your key here or generate a new one"
                        autoComplete="off"
                        value={cryptoKey}
                        onChange={onChangeCryptoKey}/>
                    {cryptoKey &&
                        <button onClick={() => navigator.clipboard.writeText(cryptoKey)} className="copy-button">
                            <img className="copy-icon" src={copyIcon} alt="copyToClipboard" />
                        </button>
                    }
                </div>
            <button className="submit-button crypto-item" onClick={generateKey}>
                <span>Generate Random Key</span>
            </button>
            <button className="submit-button green crypto-item" onClick={redirectToFiles} disabled={cryptoKey.length !== 64}>
                <span>Go see my files!</span>
            </button>
            </div>
        </div>
    )
}

export default Crypto;