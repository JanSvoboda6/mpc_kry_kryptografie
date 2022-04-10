import React, {useState} from "react";
import FileHandler from "../file/FileHandler";
import Navbar from "../navigation/Navbar";
import HelperBox from "../navigation/HelperBox";

function FileHandlerPage()
{
    const [warning, setWarning] = useState("");
    const onWarningClose = () => {
        setWarning("");
    }
    return (
        <div>
            <div className="wrapper">
            <HelperBox content={'Your secret key is: ' + localStorage.getItem('crypto_key')} onClose={() => null}/>
            {warning && <HelperBox content={warning} warning={true} onClose={onWarningClose}/>}
            <Navbar/>
            </div>
            <FileHandler onWarning={warningMessage => setWarning(warningMessage)} />
        </div>
    )
}

export default FileHandlerPage;