import React, {useEffect, useState} from "react"
import FileBrowser, {Icons} from "react-keyed-file-browser";
import Moment from "moment";
import "../../styles/FileHandler.css";
import "font-awesome/css/font-awesome.min.css";
import {AxiosResponse} from "axios";
import {FileInformation} from "../../types";
import FileService from "./FileService";
import loadingAnimation from "../../styles/loading_graphics.gif";
import FadeIn from "react-fade-in";
import FileUtility from "./FileUtility";
import CryptoService from "../crypto/CryptoService";
import aesjs from "aes-js";

/**
 *  Class for an interaction with files and folders.
 */
function FileHandler(props)
{
    const [isLoaded, setLoaded] = useState(false);
    const [files, setFiles] = useState<FileInformation[]>([]);

    useEffect(() =>
    {
        FileService.getFiles()
            .then(
                (response: AxiosResponse<any>) =>
                {
                    const files: Array<FileInformation> = [];
                    if (response.data)
                    {
                        response.data.forEach(file =>
                        {
                            const fileName = hexToAscii(CryptoService.decrypt(aesjs.utils.hex.toBytes(file.key)));
                            if (fileName.endsWith("/"))
                            {
                                files.push({key: fileName});
                                return;
                            }
                            file.key = fileName;
                            files.push(file);
                        });
                    }
                    setFiles(files);
                    setLoaded(true);
                },
                (error) =>
                {
                    setLoaded(true);
                }
            )
    }, [])

    const handleCreateFolder = (key: string) =>
    {
        const folder: FileInformation = {key: key};
        setFiles(folders => [...folders, folder]);
        const folderEncrypted: FileInformation = {key: CryptoService.encrypt(aesjs.utils.utf8.toBytes(key))};
        FileService.createFolder(folderEncrypted);
    }

    const handleCreateFiles = (addedFiles: File[], prefix: string) =>
    {
        for(let i = 0; i < addedFiles.length; i++)
        {
            if (!addedFiles[i].name.includes("."))
            {
                props.onWarning("Dear user, please drag & drop files only. You can create a folder by clicking on 'Add Folder' button.");
                return;
            }
        }
        setLoaded(false);
        let uniqueAddedFiles: FileInformation[] = FileUtility.getUniqueAddedFiles(files, addedFiles, prefix);
        let uniqueAddedFileEncrypted: any;

        uniqueAddedFiles.forEach(async file => {
            // @ts-ignore
            await file.data?.arrayBuffer().then(buffer =>{
                uniqueAddedFileEncrypted = {
                    "key":  CryptoService.encrypt(aesjs.utils.utf8.toBytes(file.key)),
                    "data": CryptoService.encryptBytesFormat(new Uint8Array(buffer))
                };
                const createdFile = new File([uniqueAddedFileEncrypted.data], uniqueAddedFileEncrypted.key);
                FileService.uploadFiles([createdFile]);
                });
            });
        setFiles(existingFiles => [...existingFiles, ...uniqueAddedFiles])
        setLoaded(true);
    }

    const handleDeleteFolders = (folderKeys: string[]) =>
    {
        const keysOfFilesAndFoldersToBeDeleted = FileUtility.getContentOfFolders(files, folderKeys);

        folderKeys.forEach(folderKey => {
            if(!keysOfFilesAndFoldersToBeDeleted.includes(folderKey))
            {
                keysOfFilesAndFoldersToBeDeleted.push(folderKey);
            }
        });

        FileService.deleteFiles(keysOfFilesAndFoldersToBeDeleted.map(fileKey => CryptoService.encrypt(aesjs.utils.utf8.toBytes(fileKey)))).then(() => {
            setFiles(FileUtility.deleteSelectedFolders(files, folderKeys));
        });
    }

    const handleDeleteFiles = (fileKeys: string[]) =>
    {
        FileService.deleteFiles(fileKeys.map(fileKey => CryptoService.encrypt(aesjs.utils.utf8.toBytes(fileKey)))).then(() => {
            setFiles(FileUtility.deleteSelectedFiles(files, fileKeys));
        });
    }

    const handleDownloadFile = (keys: string[]) => {
        download(keys);
    }

    const handleDownloadFolder = (keys: string[]) => {
        download(keys);
    }

    function getKeysOfFilesToDownload(keys: string[]) {
        let keysOfFilesToDownload: string[] = []
        keys.forEach(key => {
            if (key.endsWith("/"))
            {
                FileUtility.getAllFilesInFolder(files, key).forEach(fileKey => {
                    if (!keysOfFilesToDownload.includes(fileKey))
                    {
                        keysOfFilesToDownload.push(fileKey);
                    }
                })
            } else {
                if (!keysOfFilesToDownload.includes(key))
                {
                    keysOfFilesToDownload.push(key);
                }
            }
        });
        return keysOfFilesToDownload;
    }

    const download = (keys: string[]) => {
        const keysOfFilesToDownload = getKeysOfFilesToDownload(keys);

        let namesMap : Map<string, string> = new Map();

        keysOfFilesToDownload.forEach(key => {
            namesMap.set(key, CryptoService.encrypt(aesjs.utils.utf8.toBytes(key)));
        })

        namesMap.forEach((encryptedKey, key) => {
            FileService.downloadFile(encryptedKey).then(response => {
                let fileReader = new FileReader();
                fileReader.onload = function(event) {
                    // @ts-ignore
                    let bytes = CryptoService.decryptBytesFormat(new Uint8Array(event.target.result));
                    const blob = new Blob([bytes], );
                    const link = document.createElement("a");
                    link.href = window.URL.createObjectURL(blob);
                    link.download = key.substr(key.lastIndexOf("/") + 1, key.length);
                    link.click();
                };
                fileReader.readAsArrayBuffer(response.data);
            });
        });
    }

    if (!isLoaded)
    {
        return <FadeIn>
            <div className="loading-animation-wrapper">
                <img className="dataset-loading-animation" src={loadingAnimation} alt="loadingAnimation"/>
            </div>
        </FadeIn>
    }
    return (
        <div>
            <FadeIn>
                <div className="file-editor-wrapper">
                    <FileBrowser
                        files={files.map(file =>
                        {
                            const modifiedTimeInUnixFormat = file.modified ? file.modified : 0;
                            if (modifiedTimeInUnixFormat !== 0)
                            {
                                const modified = Moment.duration(modifiedTimeInUnixFormat * 1000);
                                return ({
                                    key: file.key,
                                    modified: +modified,
                                    size: file.size
                                })
                            }
                            return ({
                                key: file.key
                            })
                        })}
                        icons={Icons.FontAwesome(4)}
                        detailRenderer={() => null}
                        onCreateFolder={handleCreateFolder}
                        onCreateFiles={handleCreateFiles}
                        onDeleteFolder={handleDeleteFolders}
                        onDeleteFile={handleDeleteFiles}
                        onDownloadFile={handleDownloadFile}
                        onDownloadFolder={handleDownloadFolder}
                    />
                </div>
            </FadeIn>
        </div>
    )
}

function hexToAscii(hexadecimal)
{
    const hex = hexadecimal.toString();
    let str = "";
    for (let n = 0; n < hex.length; n += 2) {
        str += String.fromCharCode(parseInt(hex.substr(n, 2), 16));
    }
    return str;
}

export default FileHandler;