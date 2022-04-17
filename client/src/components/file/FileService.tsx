import axios from "axios";
import { FileInformation } from "../../types";
import authorizationHeader from "../../services/AuthorizationHeader";
import {API_URL} from "../../helpers/BackendApi";

const getFiles = () =>
{
    return axios.get(API_URL + '/api/dataset', {headers: authorizationHeader()});
}

const createDirectory = (directory: FileInformation) =>
{
    return axios.post(
        API_URL + '/api/dataset/createdirectory',
        directory,
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

    return axios.post(API_URL + '/api/dataset/uploadfiles', formData, { headers: authorizationHeader() }, );
}

const deleteFolders = (keys: string[]) => {
    return axios.post(
        API_URL + '/api/dataset/folders/delete',
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
        API_URL + '/api/dataset/files/delete',
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

const moveFile = (oldKey: string, newKey: string) => {
    return axios.post(
        API_URL + '/api/dataset/files/move',
        {oldKey, newKey},
        {
            headers:
                {
                    'Authorization': authorizationHeader()['Authorization'],
                    'Content-type': 'application/json; charset=utf-8'
                }
        }
    );
}

const moveFolder = (oldKey: string, newKey: string) => {
    return axios.post(
        API_URL + '/api/dataset/folders/move',
        {oldKey, newKey},
        {
            headers:
                {
                    'Authorization': authorizationHeader()['Authorization'],
                    'Content-type': 'application/json; charset=utf-8'
                }
        }
    );
}

const download = (key: string) => {
    return axios.post<any>(
        API_URL + '/api/dataset/download',
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

export default { getFiles, createDirectory, uploadFiles, deleteFolders, deleteFiles, moveFile, moveFolder, download }