import React from 'react';
import FileUtility from "../../components/file/FileUtility";

describe('Creating files', () => {
    test('When creating new files then only unique new files are returned', () => {
        const existingFiles = [
            {
                'key': 'aaa.txt',
                'size': undefined,
                'data': undefined
            }
        ];
        const addedFiles = [
            {
                'name': 'aaa.txt',
                'size': undefined,
                'data': undefined
            },

            {
                'name': 'bbb.txt',
                'size': undefined,
                'data': undefined
            }
        ];

        const prefix = '';
        const uniqueAddedFiles = JSON.stringify(FileUtility.getUniqueAddedFiles(existingFiles, addedFiles, prefix));
        expect(uniqueAddedFiles).not.toMatch(/aaa.txt/i);
        expect(uniqueAddedFiles).toMatch(/bbb.txt/i);
    })
});

describe("Deleting files and folders", () => {
    test("When deleting folder then all files of the folder are deleted", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            }
        ];

        const keysOfFoldersToBeDeleted = ['AAA'];

        const remainingFiles = FileUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([]);
    });

    test("When deleting folder then all child folders and files of the folder are deleted", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/BBB/bbb.txt',
            }
        ];

        const keysOfFoldersToBeDeleted = ['AAA'];

        const remainingFiles = FileUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([]);
    })

    test("When deleting folder then parent folders and files are not deleted", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/BBB/bbb.txt',
            }
        ];

        const keysOfFoldersToBeDeleted = ['AAA/BBB/'];

        const remainingFiles = FileUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([{'key': 'AAA/aaa.txt'}]);
    });

    test("When deleting multiple folders then all child folders and files are deleted", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'BBB/bbb.txt',
            },
        ];

        const keysOfFoldersToBeDeleted = ['AAA/', 'BBB/'];

        const remainingFiles = FileUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([]);
    });

    test("When deleting multiple folders then parent folders and files are not deleted", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/BBB/bbb.txt',
            },
            {
                'key': 'AAA/CCC/ccc.txt',
            }
        ];

        const keysOfFoldersToBeDeleted = ['AAA/BBB/', 'AAA/CCC/'];

        const remainingFiles = FileUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([{'key': 'AAA/aaa.txt'}]);
    });

    test("When deleting multiple folders with deleting the parent first then all folders and files are deleted", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/BBB/bbb.txt',
            },
            {
                'key': 'AAA/CCC/ccc.txt',
            }
        ];

        const keysOfFoldersToBeDeleted = ['AAA/', 'AAA/CCC/'];

        const remainingFiles = FileUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([]);
    });

    test("When deleting folders without files then all child folders are deleted", () => {
        const existingFiles = [
            {
                'key': 'AAA/',
            },
            {
                'key': 'AAA/BBB/',
            },
            {
                'key': 'AAA/BBB/CCC',
            }
        ];

        const keysOfFoldersToBeDeleted = ['AAA/BBB/'];

        const remainingFiles = FileUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([{'key': 'AAA/'}]);
    });

    test("When deleting non existing folders then all existing files remain", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/BBB/bbb.txt',
            }
        ];

        const keysOfFoldersToBeDeleted = ['CCC/DDD/'];

        const remainingFiles = FileUtility.deleteSelectedFolders(existingFiles, keysOfFoldersToBeDeleted);
        expect(remainingFiles).toEqual([{'key': 'AAA/aaa.txt'}, {'key': 'AAA/BBB/bbb.txt'}]);
    });

    test("When deleting a file then file is not present anymore", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/BBB/bbb.txt',
            }
        ];
        const keyOfFileTobeDeleted = ['AAA/BBB/bbb.txt'];
        const remainingFiles = FileUtility.deleteSelectedFiles(existingFiles, keyOfFileTobeDeleted);
        expect(remainingFiles).toEqual([{'key': 'AAA/aaa.txt'}]);
    });

    test("When deleting a multiple files then files are not present anymore", () => {
        const existingFiles = [
            {
                'key': 'AAA/aaa.txt',
            },
            {
                'key': 'AAA/BBB/bbb.txt',
            },
            {
                'key': 'AAA/CCC/ccc.txt'
            }
        ];
        const keyOfFileTobeDeleted = ['AAA/BBB/bbb.txt', 'AAA/CCC/ccc.txt'];
        const remainingFiles = FileUtility.deleteSelectedFiles(existingFiles, keyOfFileTobeDeleted);
        expect(remainingFiles).toEqual([{'key': 'AAA/aaa.txt'}]);
    });
});

describe("Listing content of folders", () => {
    test("When getting a content of single folder then all content is returned", () => {
        const existingFiles = [
            {
                'key': 'AAA/',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'AAA/aaa.txt',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'AAA/bbb.txt',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'AAA/BBB/',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'AAA/BBB/ccc.txt',
                'size': undefined,
                'data': undefined
            },
        ];

        const folderKey = 'AAA/';

        const content = FileUtility.getContentOfFolders(existingFiles, [folderKey]);
        expect(content).toEqual(['AAA/aaa.txt', 'AAA/bbb.txt', 'AAA/BBB/', 'AAA/BBB/ccc.txt']);

    });

    test("When getting a content of multiple folders then all content is returned", () => {
        const existingFiles = [
            {
                'key': 'AAA/',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'AAA/aaa.txt',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'BBB/',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'BBB/bbb.txt',
                'size': undefined,
                'data': undefined
            },
        ];

        const folderKeys = ['AAA/', 'BBB/'];

        const content = FileUtility.getContentOfFolders(existingFiles, folderKeys);
        expect(content).toEqual(['AAA/aaa.txt','BBB/bbb.txt']);

    });

    test("When getting a content of multiple folders then no duplicate content is returned", () => {
        const existingFiles = [
            {
                'key': 'AAA/',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'AAA/aaa.txt',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'AAA/bbb.txt',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'AAA/BBB/',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'AAA/BBB/ccc.txt',
                'size': undefined,
                'data': undefined
            },
        ];

        const folderKeys = ['AAA/', 'AAA/BBB/'];

        const content = FileUtility.getContentOfFolders(existingFiles, folderKeys);
        expect(content).toEqual(['AAA/aaa.txt', 'AAA/bbb.txt', 'AAA/BBB/', 'AAA/BBB/ccc.txt']);

    });


    test("When getting a content of child folders then no content specific to parent is returned", () => {
        const existingFiles = [
            {
                'key': 'AAA/',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'AAA/aaa.txt',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'AAA/bbb.txt',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'AAA/BBB/',
                'size': undefined,
                'data': undefined
            },
            {
                'key': 'AAA/BBB/ccc.txt',
                'size': undefined,
                'data': undefined
            },
        ];

        const folderKey = 'AAA/BBB/';

        const content = FileUtility.getContentOfFolders(existingFiles, [folderKey]);
        expect(content).toEqual(['AAA/BBB/ccc.txt']);

    });
});

