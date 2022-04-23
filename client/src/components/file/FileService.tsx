import axios from "axios";
import { FileInformation } from "../../types";
import authorizationHeader from "../../services/AuthorizationHeader";
import {API_URL} from "../../helpers/BackendApi";

/**
 * Class for sending file/folder based requests to the backend API.
 */
const getFiles = () =>
{
    return axios.get(API_URL + '/api', {headers: authorizationHeader()});
}

const createFolder = (folder: FileInformation) =>
{
    return axios.post(
        API_URL + '/api/folder/create',
        folder,
        {
            headers: 
            {
                'Authorization': authorizationHeader()['Authorization'],
                'Content-type': 'application/json; charset=utf-8'
            }
        }
    );
};

const uploadFiles = (files: File[]) =>
{
    let formData = new FormData();
    let keys: any = [];

    files.forEach(file =>
    {
        keys.push(file.name);
    });

    let jsonLabelData = {
        'keys': keys
    };

    formData.append(
        'keys',
        new Blob([JSON.stringify(jsonLabelData)], {
            type: 'application/json'
        }));

    for (let key of Object.keys(files))
    {
        if (key !== 'length')
        {
            formData.append('files', files[key]);
        }
    }

    return axios.post(API_URL + '/api/file/upload', formData, { headers: authorizationHeader() }, );
}

const deleteFolders = (keys: string[]) => {
    return axios.post(
        API_URL + '/api/folder/delete',
        keys,
        {
            headers:
                {
                    'Authorization': authorizationHeader()['Authorization'],
                    'Content-type': 'application/json; charset=utf-8'
                }
        }
    );
}

const deleteFiles = (keys: string[]) => {
    return axios.post(
        API_URL + '/api/file/delete',
        keys,
        {
            headers:
                {
                    'Authorization': authorizationHeader()['Authorization'],
                    'Content-type': 'application/json; charset=utf-8'
                }
        }
    );
}

const downloadFile = (key: string) => {
    return axios.post<any>(
        API_URL + '/api/file/download',
        key,
        {
            headers:
                {
                    'Authorization': authorizationHeader()['Authorization'],
                    'Content-type': 'application/json; charset=utf-8'
                },
            responseType: 'blob'
        }
    );
}

export default { getFiles, createFolder, uploadFiles, deleteFolders, deleteFiles, downloadFile }