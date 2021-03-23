
public class TLB //TLB class for keeping the small TLB up to date
{
	private int page[];
	private int frame[];
	private int index;
	
	public TLB(int page[],int frame[], int index) //constructor for our TLB
	{
		this.page = page;
		this.frame = frame;
		this.index = index;
	}
	
	public int find(int page_number) //find to see if the page is currently in the TLB, return the value stored if so
	{
		int value = -1;
		
		for(int i=0; i<16; i++)
		{
			if(page[i] == page_number) 
			{
				value = frame[i];
				return value;
			}
		}
		return value;
	}

	public void update(int page_number, int page_frame) //add the page number and page frame to the TLB, replacing old data.
	{
		page[index] = page_number;
		frame[index] = page_frame;
		
		index++;
		index = index % 16;
	}
}
