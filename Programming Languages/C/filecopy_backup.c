/*

Nathan Dalal, 4/29/14

Mainpulating a Binary .WAV File

This piece of C code copies a binary file (like a .wav file).
It saves the data of the file in a memory structure.
Manipulation is done on the bytes of the memory structure and this new information is saved 
by creating a new file based on the new information stored in the memory structure.

*/

//Include Statements
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>
#include "atcs.h"

//Structure Declarations
union longchar
     {
     int v;
     char c[4];
     };

struct chunk
     {
     union longchar chunkID;
     int chunkSize;
     union longchar chunkFormat;
     };

struct subchunk1
	{
	union longchar subchunk1ID;
	int subchunk1Size;
	short audioFormat;
	short numChannels;
	int sampleRate;
	int byteRate;
	short blockAlign;
	short bitsPerSample;
	};

struct subchunk2
	{
	union longchar subchunk2ID;
	int subchunk2Size;
	BYTE data[8];
	};

struct wav 
     {
     struct chunk chu;
     struct subchunk1 sc1;
     struct subchunk2 sc2;
     };

struct mem
    {
    off_t size; //size of the file in bytes
    struct wav data; //place holder for the bytes from the file
    };

//Function Declarations
struct mem* readFile (char* filename);
off_t fileSize (int hfile);
int processFile (struct mem* pmem);
void createFile (struct mem* pmem, char* filename);
void housekeeping (struct mem* pmem);
int main (int argc, char* argv[]);

//Functions
/*
	readFile

	This function reads a binary file indicated by a passed filename.
	This is done by opening the file, allocating memory for the total bytes of the file and the mem structure,
	and reading the entire file into the previously defined mem structure.

	The size of the file is retrieved through a fileSize function, and the contents of the file
	are read into the BYTE array contained within the mem structure.

	@param filename the name of the file to be read
	@return pointer to mem structure containing the file
*/
struct mem* readFile (char* filename)
	{
	int hfile;
	off_t fsize;
	struct mem* pmem = (struct mem*)NIL;
	off_t nbytes;

	hfile = open (filename, O_RDONLY | O_BINARY);
	if (hfile != -1) //keep going if file open was successful
		{
		fsize = fileSize(hfile);
		pmem = (struct mem*)(malloc(fsize+sizeof(struct mem)));
		if (pmem != (struct mem*)NIL) //keep going if malloc was successful
			{
			pmem->size=fsize;
			nbytes = read(hfile, &pmem->data.chu.chunkID, fsize);
			if (nbytes != fsize)
				{
				free(pmem);
				pmem = (struct mem*)NIL; //indicates reading failure
				printf("Error %d reading file %s.\n", errno, filename);
				}
			}
		else printf("Error %d allocating memory to store file %s.\n", errno, filename);
		close(hfile);
		}
	else printf("Error %d opening file %s.\n", errno, filename);
	return(pmem);
	}

/*
	fileSize

	Given a handle to a file open for reading, fileSize will return the number of bytes in the file.

	The lseek method allows the method to find the number of bytes in the file without altering the file.
	The current position in the file is recorded. 
	Then the file is moved to the end position, which tells us how many bytes there are in the file. 
	The position in the file is then returned to the previous current position.

	The return type is off_t as this is the return type of the lseek method.

	@param hfile the valid file handle to access the file
	@return the number of bytes in the file stored as an off_t value
*/
off_t fileSize (int hfile)
	{
    off_t currentPos, length;

	currentPos = lseek(hfile, (off_t)0, SEEK_CUR);
	length = lseek(hfile, (off_t)0, SEEK_END);
	lseek(hfile, currentPos, SEEK_SET);

	return(length);
	}

/*
	processFile

	@param pmem the pointer to the mem structure containing the file to be altered
	@return 
*/
int processFile (struct mem* pmem)
	{
	return(0);
	}

/*
	createFile

	Given a pointer to a mem structure containing a file and a filename,
	this method will create a new binary file with the file name passed and
	its contents contained in the pointer to the mem structure.

	This method will open the file for read and write purposes.
	It will write to the file with the bytes of data in the mem structure.

	@param pmem the pointer to the mem structure containing the file to be created
	@param filename the name of the file to be created
*/
void createFile (struct mem* pmem, char* filename)
	{
	int hfile;

	hfile = open (filename, O_BINARY | O_RDWR | O_CREAT | O_EXCL, S_IWRITE | S_IREAD);
	if (hfile != -1)
		{
		write(hfile, &pmem->data.chu.chunkID, pmem->size);
		close(hfile);
		}
	else printf("Error %d creating file %s.\n", errno, filename);
	return;
	}

/*
	housekeeping

	Memory must be properly released in C to prevent memory leaks.
	This method frees the memory pmem is pointing to, and it also zeroes the pointer.

	This will free the memory created in the readFile method using the malloc construct.

	@param pmem the pointer to the mem structure to free and zero
*/
void housekeeping (struct mem* pmem)
	{
	free(pmem);
	return;
	}

/*
	main

	The file is read into a mem structure using the readFile method.
	Alterations are done in the processFile method.
	The alterations will be saved in a newly created file in the createFile method.
	Memory will be freed in the housekeeping method.
*/
int main(int argc, char* argv[])
    {
    struct mem* pmem;

    pmem = readFile("WELCOME.wav");
    if (pmem != (struct mem*)NIL)
    	{
    	processFile(pmem);
    	createFile(pmem, "copyfile.wav");
    	housekeeping(pmem);
    	}

    //error messages are within methods
    exit(0);
    }