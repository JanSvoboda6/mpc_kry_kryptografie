import React, {useState} from "react";
import FileHandler from "../file/FileHandler";
import Navbar from "../navigation/Navbar";
import HelperBox from "../navigation/HelperBox";

/**
 * File handler parent page. It constructs a page with helper box, navigation bar and file handler.
 */
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