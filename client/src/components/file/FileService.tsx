import axios from "axios";
import { FileInformation } from "../../types";
import authorizationHeader from "../../services/AuthorizationHeader";
const API_URL = "http://localhost:8080/api/dataset";

const getFiles = () =>
{
    return axios.get(API_URL, {headers: authorizationHeader()});
}

const createDirectory = (directory: FileInformation) =>
{
    return axios.post(
        API_URL + '/createdirectory',
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

const uploadFiles = (file: FileInformation) =>
{
    const data = {
        "keys": [file.key],
        "files": [file.data]
    }

    return axios.post(API_URL + '/upload', data, { headers: authorizationHeader() }, );
}

const deleteFolders = (keys: string[]) => {
    return axios.post(
        API_URL + '/folders/delete',
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
        API_URL + '/files/delete',
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
        API_URL + '/files/move',
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
        API_URL + '/folders/move',
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

const download = (keys: string[]) => {
    return axios.post<any>(
        API_URL + '/download',
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

export default { getFiles, createDirectory, uploadFiles, deleteFolders, deleteFiles, moveFile, moveFolder, download }