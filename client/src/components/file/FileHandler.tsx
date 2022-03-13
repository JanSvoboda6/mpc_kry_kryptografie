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
                            if (file.key.endsWith('/'))
                            {
                                files.push({key: file.key});
                                return;
                            }
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
        setLoaded(false);
        const folder: FileInformation = {key: key};
        setFiles(folders => [...folders, folder]);
        FileService.createDirectory(folder).then(() => setLoaded(true));
        setLoaded(true);
    }

    const handleCreateFiles = (addedFiles: File[], prefix: string) =>
    {
        const uniqueAddedFiles: FileInformation[] = FileUtility.getUniqueAddedFiles(files, addedFiles, prefix);
        FileService.uploadFiles(uniqueAddedFiles).then(() => setFiles(existingFiles => [...existingFiles, ...uniqueAddedFiles]));
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

    const download = (keys: string[]) => {
        FileService.download(keys).then(response => {
            const type = response.headers['content-type']
            const blob = new Blob([response.data], {type: type})
            const link = document.createElement('a')
            link.href = window.URL.createObjectURL(blob)
            console.log(keys)
            link.download = keys[0]
            link.click()
        });
    }

    const handleFolderSelection = (folder) =>
    {
        if (props.handleFolderSelection)
        {
            props.handleFolderSelection(folder)
        }
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

                        onCreateFolder={handleCreateFolder}
                        onCreateFiles={handleCreateFiles}
                        onSelectFolder={(folder) => handleFolderSelection(folder)}
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