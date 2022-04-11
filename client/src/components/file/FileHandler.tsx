import React, {useEffect, useState} from 'react'
import FileBrowser, {Icons} from 'react-keyed-file-browser';
import Moment from 'moment';
import '../../styles/FileHandler.css';
import 'font-awesome/css/font-awesome.min.css';
import {AxiosResponse} from 'axios';
import {FileInformation} from '../../types';
import FileService from './FileService';
import loadingAnimation from "../../styles/loading_graphics.gif";
import FadeIn from 'react-fade-in';
import FileUtility from "./FileUtility";
import CryptoService from "../crypto/CryptoService";
import aesjs from "aes-js";

function hexToAscii(hexadecimal)
{
    const hex = hexadecimal.toString();
    let str = '';
    for (let n = 0; n < hex.length; n += 2) {
        str += String.fromCharCode(parseInt(hex.substr(n, 2), 16));
    }
    return str;
}

function FileHandler(props)
{
    const [isLoaded, setLoaded] = useState(false);
    const [files, setFiles] = useState<FileInformation[]>([]);
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() =>
    {
        FileService.getFiles()
            .then(
                (res: AxiosResponse<any>) =>
                {
                    const files: Array<FileInformation> = [];
                    if (res.data)
                    {
                        res.data.forEach(file =>
                        {
                            const fileName = hexToAscii(CryptoService.decrypt(aesjs.utils.hex.toBytes(file.key)));
                            if (fileName.endsWith('/'))
                            {
                                files.push({key: fileName});
                                return;
                            }
                            file.key = fileName
                            files.push(file);
                        });
                    }
                    setFiles(files);
                    setLoaded(true);
                },
                (error) =>
                {
                    setErrorMessage(error.message);
                    setLoaded(true);
                }
            )
    }, [])

    const handleCreateFolder = (key: string) =>
    {
        const folder: FileInformation = {key: key};
        setFiles(folders => [...folders, folder]);
        const folderEncrypted: FileInformation = {key: CryptoService.encrypt(aesjs.utils.utf8.toBytes(key))};
        FileService.createDirectory(folderEncrypted);
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
                    'key':  CryptoService.encrypt(aesjs.utils.utf8.toBytes(file.key)),
                    'data': CryptoService.encryptBytesFormat(new Uint8Array(buffer))
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
        FileService.deleteFolders(folderKeys).then( () => {
            setFiles(FileUtility.deleteSelectedFolders(files, folderKeys));
       });
    }

    const handleDeleteFiles = (fileKeys: string[]) =>
    {
        //DatasetService.deleteFiles(fileKeys).then( () => {
            setFiles(FileUtility.deleteSelectedFiles(files, fileKeys));
        //});
    }

    const handleMoveFile = (oldKey: string, newKey: string) =>
    {
       // DatasetService.moveFile(oldKey, newKey).then(() => {
            setFiles(FileUtility.moveFile(files, oldKey, newKey));
       // })
    }

    const handleMoveFolder = (oldKey: string, newKey: string) =>
    {
       // DatasetService.moveFolder(oldKey, newKey).then(() => {
            setFiles(FileUtility.moveFolder(files, oldKey, newKey));
        //})
    }

    const handleDownloadFile = (keys: string[]) => {
        download(keys);
    }

    const handleDownloadFolder = (keys: string[]) => {
        download(keys);
    }

    const getAllFilesInFolder = (folderKey: string): string[] =>
    {
        let keysOfFilesToDownload: string[] = [];
        files.forEach(file => {
            if (file.key !== folderKey && file.key.substr(0, folderKey.length) === folderKey)
            {
                if(file.key.endsWith("/"))
                {
                    getAllFilesInFolder(file.key).forEach(fileKey => {
                        if(!keysOfFilesToDownload.includes(fileKey))
                        {
                            keysOfFilesToDownload.push(fileKey);
                        }
                    });
                }
                else if (!keysOfFilesToDownload.includes(file.key))
                {
                    keysOfFilesToDownload.push(file.key);
                }
            }
        });
        return keysOfFilesToDownload;
    }

    const download = (keys: string[]) => {
        let keysOfFilesToDownload: string[] = [];

        keys.forEach(key => {
            if (key.endsWith("/"))
            {
                getAllFilesInFolder(key).forEach(fileKey => {
                    if(!keysOfFilesToDownload.includes(fileKey))
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

        let namesMap : Map<string, string> = new Map();

        keysOfFilesToDownload.forEach(key => {
            namesMap.set(key, CryptoService.encrypt(aesjs.utils.utf8.toBytes(key)));
        })

        namesMap.forEach((encryptedKey, key) => {
            FileService.download(encryptedKey).then(response => {
                let fileReader = new FileReader();
                fileReader.onload = function(event) {
                    // @ts-ignore
                    let bytes = CryptoService.decryptBytesFormat(new Uint8Array(event.target.result));
                    const blob = new Blob([bytes], );
                    const link = document.createElement('a');
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
            <div className='loading-animation-wrapper'>
                <img className='dataset-loading-animation' src={loadingAnimation} alt="loadingAnimation"/>
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
                        //onMoveFolder={(oldKey, newKey) => handleMoveFolder(oldKey, newKey)}
                        //onMoveFile={(oldKey, newKey) => handleMoveFile(oldKey, newKey)}
                        //onRenameFolder={(oldKey, newKey) => handleMoveFolder(oldKey, newKey)}
                        //onRenameFile={(oldKey, newKey) => handleMoveFile(oldKey, newKey)}
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

export default FileHandler;