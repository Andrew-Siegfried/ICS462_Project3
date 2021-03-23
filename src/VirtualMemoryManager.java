import java.io.*;
import java.util.*;

public class VirtualMemoryManager 
{
	
	public static void main(String[] args) 
	{
		String fileName;
		String binName = null;
		if (args.length < 1) {
            System.out.println("Pass in addresses.txt as param 1 and backing_store.bin as param 2.");
            fileName = "addresses.txt"; //used for personal testing to default files
            binName = "BACKING_STORE.bin";
        }
		else
		{
			fileName = args[0];
			binName = args[1];
		}
		int page_table[] = new int[256];
		int memory[] = new int[(256*256)];
		int tlb_index = 0;
		TLB tlb = new TLB(new int[16],new int[16],tlb_index);
		int page_fault_rate = 0;
		int frames = 0;
		int tlb_hit_rate = 0;
		
		for(int i=0; i<256; i++)
		{
			page_table[i] = -1; //fill table to show if its unused
		}
		
		try 
		{
			Scanner input_data = new Scanner(new File(fileName));
			RandomAccessFile backing_store = new RandomAccessFile(new File(binName), "r"); //https://docs.oracle.com/javase/7/docs/api/java/io/RandomAccessFile.html
			int line = 0;
			
			while (input_data.hasNext()) 
			{ 
				String input_line = input_data.nextLine();
				int logical_address = Integer.valueOf(input_line);
				int physical_address = 0;
				int page_number = (logical_address >> 8); //get page number from input line
		        int offset = logical_address & 255; //get offset from input_line
		        int frame = -1; //if the frame still reads -1 then we can go to the next method of checking
				
				frame = tlb.find(page_number); //attempt to find the page in TLB
				if(frame != -1)
				{
					tlb_hit_rate++; //if we get here that means the page was found in the TLB and the frame was returned
					physical_address = frame*256 + offset;
				}
				else
				{
					frame = page_table[page_number]; //check to see if its in the page table
					if(frame == -1) //load from bin file
					{
						page_fault_rate++;
						byte page[] = new byte[256];
						backing_store.seek(page_number*256); //jump to the page location
						backing_store.read(page); //read in a page of 256 bytes
						frame = frames % 256; //set the new frame
						frames++;
						page_table[page_number] = frame; //update the page table
						int start = frame*256;
						for(int i=0; i<256; i++)
						{
							memory[(start+i)] = page[i]; //add it to memory
						}
					} 
					physical_address = frame*256 + offset; //get physical address from the new frame and read in offset
					tlb.update(page_number, frame); //update the TLB with page number and the new frame
				}	//below is the printout of virtual address / physical address and the value stored in memory
				System.out.println("Virtual address: " + logical_address + " Physical address: " + physical_address + " Value: "+ memory[physical_address]);
				line++; //increment line read in
			}
			
			float percent_page_fault = (float) page_fault_rate / (float) line; //calculate percentages
			float percent_tlb_hit = (float) tlb_hit_rate / (float) line;
			
			System.out.printf("Page-fault rate: %.2f%s\n", percent_page_fault*100.0, "%"); //output the page hit rate
			System.out.printf("TLB hit rate: %.2f%s \n", percent_tlb_hit*100.0, "%");
			
			backing_store.close();
			input_data.close(); //close everything up
		} 
		catch (IOException ex) 
		{
			ex.printStackTrace();
		}
	}
}

/*


Statistics

After completion, your program is to report the following statistics:

1. Page-fault rate—The percentage of address references that resulted in page faults.
2. TLB hit rate—The percentage of address references that were resolved in the TLB.

Since the logical addresses in addresses.txt were generated randomly and do not reflect any memory access locality, do not expect to have a high TLB hit rate.


*/