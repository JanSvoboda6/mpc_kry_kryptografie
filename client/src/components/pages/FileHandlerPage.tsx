import React from "react";
import FileHandler from "../file/FileHandler";
import Navbar from "../navigation/Navbar";
import HelperBox from "../navigation/HelperBox";

function FileHandlerPage()
{
    return (
        <div>
            <div className="wrapper">
                <HelperBox content={'Your secret key is: ' + localStorage.getItem('crypto_key')}/>
            <Navbar/>
            </div>
            <FileHandler />
        </div>
    )
}

export default FileHandlerPage;