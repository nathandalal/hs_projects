/*

Nathan Dalal, 4/29/14

Manipulating a Binary .WAV File

This piece of C code copies a .wav file.
It saves the data of the file in a memory structure.
Manipulation is done on the bytes of the memory structure and this new information is saved 
by creating a new file based on the new information stored in the manipulated memory structure.

Manipulation is done via filters. 
These filters can manipulate the data by reversing the file, altering the sample rate, or other operations.
Information about each filter can be accessed from the command prompt.

Each wave file has different attributes specified within the header.
These are read into the memory structure by overlaying amother structure on the information of the file.
Problems with the header are verified and reported to the user through verification methods.
Relevant information about each file is also accessible from the command prompt.

Filters can be applied to the creation of a new file.
This new file will be an altered form of the previously read file, altered by the specified filters.
Applying any number of filters to a new file is also accessible from the command prompt.

If a new file is created, it is ensured to have correct .WAV and PCM structure, with all filters specified applied.

Enjoy!
*/

//Include Statements
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>
#include <string.h>
#include <memory.h>
#include <math.h>
#include "atcs.h"

//Structure Declarations
union intchar
     {
     int val;
     char bytes[4];
     };

struct chunk
     {
     int chunkID;
     int chunkSize;
     int chunkFormat;
     };

struct subchunk1
	{
	int subchunk1ID;
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
	int subchunk2ID;
	int subchunk2Size;
	BYTE fileData[8];
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
int processFile (struct mem* pmem, int filter);
int verifyWavFile (struct mem* pmem);
void verifyCalcsOfFile (struct mem* pmem, BOOL initialCalc);
int filter1(struct mem* pmem);
int filter2(struct mem* pmem);
int filter3(struct mem* pmem);
int filter4(struct mem* pmem);
int filter5(struct mem* pmem);
void createFile (struct mem* pmem, char* filename);
void housekeeping (struct mem* pmem);
int addFilters (struct mem* pmem, int numfilters, char* arguments[]);
void printData (struct mem* pmem);
int main (int argc, char* argv[]);

//Functions
/*
	readFile

	This function reads a binary file indicated by a passed filename.
	This is done by opening the file, allocating memory for the total bytes of the file and the mem structure,
	and reading the entire file into the previously defined mem structure.

	The size of the file is retrieved through a fileSize function, and the contents of the file
	are read into the BYTE array contained within the mem structure.

	If the user typed in a number to see what the filter does, this is the method that says what the filter does.

	@param filename the name of the file to be read
	@return pointer to mem structure containing the file
*/
struct mem* readFile (char* filename)
	{
	int hfile;
	off_t fsize;
	struct mem* pmem = (struct mem*)NIL;
	off_t nbytes;
	int menu = atoi(filename);

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
	else 
		{
		switch (menu)
			{
			case 1:
				{
				printf("Filter %d doubles the sampling rate.\n", 1);
				break;
				}
			case 2:
				{
				printf("Filter %d reverses the sound file with any number of channels or digitization rate.\n", 2);
				break;
				}
			case 3:
				{
				printf("Filter %d zeroes the least significant bit from each byte in the new file.\n", 3);
				break;
				}
			case 4:
				{
				printf("Filter %d doubles bytes in even channels and takes the square root of bytes in odd channels.\n", 4);
				break;
				}
			case 5:
				{
				printf("Filter %d halves the sampling rate.\n", 5);
				break;
				}
			default: //if user is not checking filters or reading a file
				{
				printf("Error %d opening file %s.\n", errno, filename);
				break;
				}
			}
		}
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
	@param filter the number of the appropriate filter to be applied to the file
	@return 0 if processing was successful, number of error other than 0 if not successful
*/
int processFile (struct mem* pmem, int filter)
	{
	int status = 0;
	int verification;

	verification = verifyWavFile(pmem);
	if (verification == 0)
		{
		verifyCalcsOfFile(pmem, TRUE);
		//Error messages would be printed while verifying if the file had to correct calculated fields.
		switch (filter)
			{
			case 1:
				{
				status = filter1(pmem);
				if (status == 0)
					printf("A doubling sample rate filter %d was applied.\n", filter);
				break;
				}
			case 2:
				{
				status = filter2(pmem);
				if (status == 0) 
					printf("A reversal filter %d was applied.\n", filter);
				break;
				}
			case 3:
				{
				status = filter3(pmem);
				if (status == 0)
					printf("A least significant bit filter %d was applied.\n", 3);
				break;
				}
			case 4:
				{
				status = filter4(pmem);
				if (status == 0) 
					printf("A quirky filter that varies with odd and even channels %d was applied.\n", filter);
				break;
				}
			case 5:
				{
				status = filter5(pmem);
				if (status == 0) 
					printf("A halving sample rate filter %d was applied.\n", filter);
				break;
				}
			default:
				{
				printf("Invalid filter %d in command prompt. New file could not be created.\n", filter);
				status = -1;
				break;
				}
			}
		verifyCalcsOfFile(pmem, FALSE);
		}
	else 
		{
		status = -1;
		//Error messages would be printed while verifying if the file was .WAV or PCM.
		}

	return(status);
	}

/*
	verifyWavFile

	Verifies that the file is a valid .WAV file with PCM structure.
	It makes sure IDs are valid, the size of subchunk1 is 16, and the audio format is 1.

	@param pmem the pointer to the mem structure to be verified
	@return 0 if the file is verified
			another number as the error code for the file
			will only return one error, may be multiple errors

	Error Codes (If not a Wave File):
		1 - ChunkID is not "TIFF".
		2 - Format is not "WAVE".
		3 - Subchunk1ID is not "fmt ".
		4 - Subchunk2ID is not "data".

	Error Codes (If not a PCM structure):
		5 - Subchunk1 Size is not 16.
		6 - Audio Format is not 1.
*/
int verifyWavFile (struct mem* pmem)
	{
	union intchar checker1;
	int checker2 = 16;
	short checker3 = 1;
	int status = 0;

	//WAV File
	memcpy (checker1.bytes, "RIFF", 4);
	if (checker1.val != pmem->data.chu.chunkID)
		status = 1;
	memcpy (checker1.bytes, "WAVE", 4);
	if (checker1.val != pmem->data.chu.chunkFormat)
		status = 2;
	memcpy (checker1.bytes, "fmt ", 4);
	if (checker1.val != pmem->data.sc1.subchunk1ID)
		status = 3;
	memcpy (checker1.bytes, "data", 4);
	if (checker1.val != pmem->data.sc2.subchunk2ID)
		status = 4;

	//PCM Structure
	if (checker2 != pmem->data.sc1.subchunk1Size)
    	status = 5;
	if (checker3 != pmem->data.sc1.audioFormat)
		status = 6;

	if (status != 0)
		printf("Error %d, not a WAV file or PCM structure.\n", status);
	return(status);
	}

/*
	verifyCalcsOfFile

	Verifies if the calculated fields are correct.
	If the calculated fields are incorrect, a message is printed
	and the field is overwritten with what should be there
	if the fields were correct.
	
	This exposes people who are lazy writing headers.
	This will correct the file for proper use and data manipulation.

	@param pmem the pointer to the mem structure to be verified
	@return
*/
void verifyCalcsOfFile (struct mem* pmem, BOOL initialCalc)
	{
	int checker1;
	short checker2;

	checker1 = pmem->size - 8; //Whatever is left of the rest of the file not including the chunkSize and the chunkID (8 bytes).
	if (checker1 != pmem->data.chu.chunkSize)
		{
		pmem->data.chu.chunkSize = checker1;
		if (initialCalc)
			{
			printf("WARNING. Bad calculated field in original file: chunkSize.");
			}
		}
	checker1 = pmem->data.sc1.sampleRate * pmem->data.sc1.numChannels * pmem->data.sc1.bitsPerSample / 8;
	if (checker1 != pmem->data.sc1.byteRate)
		{
		pmem->data.sc1.byteRate = checker1;
		if (initialCalc)
			{
			printf("WARNING. Bad calculated field in original file: byteRate.");
			}
		}
	checker2 = pmem->data.sc1.numChannels * pmem->data.sc1.bitsPerSample / 8;
	if (checker2 != pmem->data.sc1.blockAlign)
		{
		pmem->data.sc1.blockAlign = checker2;
		if (initialCalc)
			{
			printf("WARNING. Bad calculated field in original file: blockAlign.");
			}
		}
	checker1 = pmem->size - 44; //All of the bytes of the playable file, which is the total size of file minus the header (44 bytes).
	if (checker1 != pmem->data.sc2.subchunk2Size)
		{
		pmem->data.sc2.subchunk2Size = checker1;
		if (initialCalc)
			{
			printf("WARNING. Bad calculated field in original file: subchunk2Size.");
			}
		}
	return;
	}

/*
	filter1

	Doubles the sample rate of the file.

	@param pmem the pointer to the structure to alter
*/
int filter1 (struct mem* pmem)
	{
	pmem->data.sc1.sampleRate *= 2;

	return(0);
	}

/*
	filter2

	Reverses the sound file.

	This is done by looping over all the samples and reversing the block samples.
	This reverses the sound file by reversing the order of the blocks.
	This is done using a temporary array.

	This method first checks if the digitization rate is divisible by 4.
	It loops in reverse over all of the samples and stores the last block of the file data array in the first block of the temp array.
	It then puts the rest of the bits accordingly in reverse in the temp array.
	After this, the temp array is copied into the file data array.

	Precondition: bitsPerSample % 4 == 0

	@param pmem the pointer to the structure to alter
*/
int filter2 (struct mem* pmem)
	{
	int status = 0;
	int i, j;
	int numBytes = pmem->data.sc2.subchunk2Size;
	int bytesPerSample = pmem->data.sc1.blockAlign;
	int numSamples = numBytes / bytesPerSample;
	BYTE* temp;
	
	temp = (BYTE*)malloc(numBytes);
	if (temp != (BYTE*)NIL)
		{
		for (i = numSamples; i != 0; i--)
			for (j = 0; j < bytesPerSample; j++)
				temp[(bytesPerSample * (numSamples - i)) + j] = pmem->data.sc2.fileData[(i * bytesPerSample) - (bytesPerSample - j)];

		for (i = 0; i < numBytes; i++)
			pmem->data.sc2.fileData[i] = temp[i];
		free(temp);
		}
	else
		{
		printf("Error allocating memory for filter %d. File could not be created.\n", 2);
		status = -1;
		}

	return(status);
	}

/*
	filter3

	Makes the least significant bit of each byte 0.
	If the least significant bit is already 0, it stays at zero.

	It is interesting to see how this changes sound quality.

	@param pmem the pointer to the structure to alter
*/
int filter3 (struct mem* pmem)
	{
	int status = 0;
	int i;

	for (i = 0; i < pmem->data.sc2.subchunk2Size; i++)
		if (pmem->data.sc2.fileData[i] % 2 == 1)
			pmem->data.sc2.fileData[i] -= 1;

	return(status);
	}

/*
	filter4

	Doubles the bytes in every even channel and takes the square root of the bytes in every odd channel.

	It iterates through all the bytes, determines which channel each byte is in, and applies the appropriate alteration.

	This is a very quirky filter.

	@param pmem the pointer to the structure to alter
*/
int filter4 (struct mem* pmem)
	{
	int status = 0;
	int i, j;
	double squareRoot;
	int currentChannel;

	for (i = 0; i < pmem->data.sc2.subchunk2Size; i++)
		{
		for (j = 0; j < pmem->data.sc1.blockAlign; j++)
			{
			currentChannel = (j / pmem->data.sc1.numChannels) + 1;
			if (currentChannel % 2 == 0)
				pmem->data.sc2.fileData[i] *= 2;
			else
				{
				squareRoot = sqrt(pmem->data.sc2.fileData[i]);
				pmem->data.sc2.fileData[i] = (int)(squareRoot);
				}
			i++;
			}
		}

	return(status);
	}

/*
	filter 5

	Halves the sampling rate, creating a slowing effect that lowers the pitch.

	@pmem the pointer to the structure to be altered
*/
int filter5 (struct mem* pmem)
	{
	pmem->data.sc1.sampleRate *= .5;

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
	addFilters

	Loops over filters in the main method.

	This also implements the "specs" subroutine that can be called from the command prompt.

	@param pmem the pointer to the mem structure to alter in a loop
	@param length is the length of the arguments array
	@param arguments are the arguments main gets from the command prompt
*/
int addFilters (struct mem* pmem, int numfilters, char* arguments[])
	{
	int status = 0;
	int i = 1;

	if (numfilters > 0)
    	{
    	while (status != -1 && i <= numfilters)
    		{
    		status = processFile(pmem, atoi(arguments[i + 2]));
    		i++;
    		}
    	}
    else
    	{
    	if (numfilters < 0)
    		printf("No output file name. File could not be created.\n");
    	else
    		{
    		if (strcmp(arguments[2], "info") == 0)
    			printData(pmem);
    		else printf("No filters in command prompt. File could not be created.\n");
    		}
    	status = -1;
    	}

    return(status);
	}

/*
	printData

	Prints out information of the file.
	This information includes file type, file size, audio format, number of channels, sample rate, digitization rate, and bytes per block.

	@param pmem the pointer to the mem structure to read data from
*/
void printData (struct mem* pmem)
	{
	printf("\tFile Type: %s\n", ".WAV");
	printf("\tFile Size: %lld Bytes\n", pmem->size);
	printf("\tAudio Format: %d\n", pmem->data.sc1.audioFormat);
	printf("\tNumber of Channels: %d\n", pmem->data.sc1.numChannels);
	printf("\tSample Rate: %d Hz\n", pmem->data.sc1.sampleRate);
	printf("\tDigitization Rate: %d\n", pmem->data.sc1.bitsPerSample);
	printf("\tBytes Per Block: %d\n", pmem->data.sc1.blockAlign);
	}

/*
	main

	The file is read into a mem structure using the readFile method.
	Alterations are done in the processFile method based on passed filters.
	The alterations will be saved in a newly created file in the createFile method.
	Memory will be freed in the housekeeping method.

	@param argc option 1: number of parameters of command prompt (should be 4 or higher for normal function)
				option 2: number of parameters could also be 2 if the user wants to know what the individual filters do
				option 3: number of parameters must be 3 to print out the information of the read file
	@param argv
		option 1:
		argument 1 - program executable file name
		argument 2 - file name to read from
		argument 3 - file name to write to
		argument 4 onward - numbers of filters to run on the file

		option 2:
		argument 1 - program executable file name
		argument 2 - filter to learn more about

		option 3:
		argument 1 - program executable file name
		argument 2 - file name to be read from
		argument 3 - "info" - indicates that user wants information about the .WAV file
*/
int main(int argc, char* argv[])
    {
    struct mem* pmem;
    int status = 0;

    if (sizeof(struct wav) == 52)
    	{
    	pmem = readFile(argv[1]);
    	if (pmem != (struct mem*)NIL)
    		{
    		status = addFilters(pmem, argc - 3, argv);
    		if (status != -1)
    			createFile(pmem, argv[2]);
    		//Error message printed within addFilters method.
    		housekeeping(pmem);
    		}
    	}
    else printf("Error. Bad byte alignment using magic number %d.\n", 52);

    //error messages are within methods
    exit(0);
    }