import java.io.*;
import java.util.*;

//AUTHORS
//ESHANTH REDDY - ec3@iastate.edu
//SIDDHARTH  - siddu123@iastate.edu


//RUNNING INSTRUCTIONS
//1. COMPILE THE CODE USING build.sh
//2. RUN THE CODE USING run.sh <path to machine code file>
//3. THE OUTPUT WILL BE IN decodedCode.txt and will be printed out on the console


public class Decoder{
	public static ArrayList<Long> instructions = new ArrayList<>();
	//public static ArrayList<String> inst = new ArrayList<>();
	public static InstructionFinder finder = new InstructionFinder();
	public static void main(String[] args) {
		//POPULATING
		finder.addInstruction("ADD", 1112, 1112, "R");
		finder.addInstruction("ADDI", 1160, 1161, "I");
		finder.addInstruction("AND", 1104, 1104, "R");
		finder.addInstruction("ANDI", 1168, 1169, "I");
		finder.addInstruction("B", 160, 191, "B");
		finder.addInstruction("CB", 672, 679, "CB");
		finder.addInstruction("BL", 1184, 1215, "B");
		finder.addInstruction("BR", 1712, 1712, "R");
		finder.addInstruction("CBNZ", 1448, 1455,"CB");
		finder.addInstruction("CBZ", 1440, 1447, "CB");
		finder.addInstruction("EOR", 1616, 1616, "R");
		finder.addInstruction("EORI", 840, 841, "I");
		finder.addInstruction("LDUR", 1986, 1986, "D");
		finder.addInstruction("LSL", 1691, 1691,"R");
		finder.addInstruction("LSR", 1690, 1690,"R");
		finder.addInstruction("ORR", 1360, 1360,"R");
		finder.addInstruction("ORRI", 712, 713,"I");
		finder.addInstruction("STUR", 1984, 1984, "D");
		finder.addInstruction("SUB", 1624, 1624,"R");
		finder.addInstruction("SUBI", 1672, 1673, "I");
		finder.addInstruction("SUBIS", 1928, 1929, "I");
		finder.addInstruction("SUBS", 1880, 1880, "R");
		finder.addInstruction("MUL", 1240, 1240, "R");
		finder.addInstruction("PRNT", 2045, 2045,"R");
		finder.addInstruction("PRNL", 2044, 2044,"R");
		finder.addInstruction("DUMP", 2046, 2046,"R");
		finder.addInstruction("HALT", 2047, 2047,"R");

		//READ THE BITS FROM THE MACHINE CODE FILE OF COMS 321 PROGRAMMING ASSIGNMENT 1
		String filePath = "";
		if (args.length > 0) {
			filePath = args[0];
		} else {
			System.out.println("Please provide a file path as a command-line argument.");
		}
		readInstructionsFromFile(filePath);

		//NOW WE SEE WHAT TO DO WITH THE INSTRUCTION
		try {
			File toWrite = new File("decodedCode.txt");
			FileWriter fw = new FileWriter(toWrite);
			int lineNumber = 0;
			for (long instruction : instructions) {

				//searching using the integer value of the first 11 bits
				Instruction data = finder.findInstruction((int)((instruction >> 21) & 0x7FF));

				if (data.getType().equals("R")) {
					decodeR(data, instruction, fw);
				} else if (data.getType().equals("I")) {
					decodeI(data, instruction, fw);
				} else if (data.getType().equals("D")) {
					decodeD(data, instruction, fw);
				} else if (data.getType().equals("CB")) {
					decodeCB(data, instruction, fw);
				} else {
					decodeB(data, instruction, fw, lineNumber);
				}
			}
			fw.close();
		}catch (IOException e){
			System.err.println(e.getMessage());
		}
	}
	public static void decodeR(Instruction data, Long ins, FileWriter outputFile) throws IOException {
		//THIS IS AN R TYPE HANDLER
		//OPCODE RM  SHANT  RN  RD
		//  11   5     6    5   5
		String command = data.getName();
		long Rm = (ins >>> 16) & 0b11111; // shift right by 16 bits and mask with 0b11111 to get the next 5 bits
		long shant = (ins >>> 10) & 0b111111; // shift right by 10 bits and mask with 0b111111 to get the next 6 bits
		long Rn = (ins >>> 5) & 0b11111; // shift right by 5 bits and mask with 0b11111 to get the next 5 bits
		long Rd = ins & 0b11111; // mask with 0b11111 to get the last 5 bits

		if (data.getName().equals("BR")) {
			outputFile.write(String.format("%s %d\n", data.getName(), Rn));
			System.out.println(String.format("%s %d\n", data.getName(), Rn));
		} else if (data.getName().equals("PRNT")) {
			outputFile.write(String.format("%s X%d\n", data.getName(), Rd));
			System.out.println(String.format("%s X%d\n", data.getName(), Rd));
		} else if (data.getName().equals("DUMP") || data.getName().equals("HALT") || data.getName().equals("PRNL")) {
			outputFile.write(String.format("%s\n", data.getName()));
			System.out.println(String.format("%s\n", data.getName()));
		} else{
			outputFile.write(String.format("%s X%d, X%d, X%d\n", data.getName(), Rd, Rn, Rm));
			System.out.println(String.format("%s X%d, X%d, X%d\n", data.getName(), Rd, Rn, Rm));
		}

	}

	public static void decodeI(Instruction data, Long ins, FileWriter outputFile) throws IOException{
		//THIS IS AN I TYPE HANDLER
		//OPCODE IMM  RN  RD
		//  10   12    5   5
		long opcode = ins >>> 22; // shift right by 22 bits to get the first 10 bits
		long Imm = (ins >>> 10) & 0b111111111111; // shift right by 10 bits and mask with 0b111111111111 to get the next 12 bits
		long Rn = (ins >>> 5) & 0b11111; // shift right by 5 bits and mask with 0b11111 to get the next 5 bits
		long Rd = ins & 0b11111; // mask with 0b11111 to get the last 5 bits

		outputFile.write(String.format("%s X%d, X%d, #%d\n", data.getName(), Rd , Rn, Imm));
		System.out.println(String.format("%s X%d, X%d, #%d\n", data.getName(), Rd , Rn, Imm));
	}

	public static void decodeD(Instruction data, Long ins, FileWriter outputFile) throws IOException {
		//THIS IS AN I TYPE HANDLER
		//OPCODE ADDRESS OP2  RN  RD
		//  11      12    9   5   5
		long opcode = ins >>> 21; // shift right by 21 bits to get the first 11 bits
		long Address = (ins >>> 12) & 0b111111111; // shift right by 12 bits and mask with 0b111111111 to get the next 9 bits
		long op2 = (ins >>> 10) & 0b11; // shift right by 10 bits and mask with 0b11 to get the next 2 bits
		long Rn = (ins >>> 5) & 0b11111; // shift right by 5 bits and mask with 0b11111 to get the next 5 bits
		long Rt = ins & 0b11111; // mask with 0b11111 to get the last 5 bits

		outputFile.write(String.format("%s X%d, [X%d, #%d]\n", data.getName(), Rt, Rn, Address));
		System.out.println(String.format("%s X%d, [X%d, #%d]\n", data.getName(), Rt, Rn, Address));
	}

	public static void decodeB(Instruction data, Long ins, FileWriter outputFile, int line) throws IOException {
		//THIS IS AN B TYPE INSTRUCTION
		//OPCODE BR_ADDRESS
		//   6       26
		//System.out.println(Long.toBinaryString(ins));
		//System.out.println(Long.toBinaryString(ins));
		long opcode = ins >>> 26; // shift right by 26 bits to get the first 6 bits
		long BR_Address = ins & 0x03FFFFFF; // mask with 0x03FFFFFF to get the last 26 bits
//        System.out.println(BR_Address);
		//System.out.println(Long.toBinaryString(BR_Address));
		int bLine = line + (int) BR_Address; //where to branch to
		//System.out.println(bLine);
		//System.out.println(data.toString());
		// System.out.println("-----------------");
		outputFile.write(String.format("%s %d\n", data.getName(), bLine));
		System.out.println(String.format("%s %d\n", data.getName(), bLine));

	}

	public static void decodeCB(Instruction data, Long ins, FileWriter outputFile) throws IOException {
		//THIS IS A CB TYPE INSTRUCTION
		//OPCODE COND_BR_ADDRESS RT
		//  8          19        5
		long opcode = ins >>> 24; // shift right by 24 bits to get the first 8 bits
		long COND_BR_Address = ins >>> 5 & 0x7FFFF; // shift right by 5 bits and mask with 0x7FFFF to get the next 19 bits
		int Rt = (int) (ins & 0x1F); // mask with 0x1F to get the last 5 bits

		String cond = "";
		switch(Rt) {
			case 0:
				cond = "EQ";
				break;

			case 1:
				cond = "NE";
				break;

			case 2:
				cond = "HS";
				break;

			case 3:
				cond = "LO";
				break;

			case 4:
				cond = "MI";
				break;

			case 5:
				cond = "PL";
				break;

			case 6:
				cond = "VS";
				break;

			case 7:
				cond = "VC";
				break;

			case 8:
				cond = "HI";
				break;

			case 9:
				cond = "LS";
				break;

			case 10:
				cond = "GE";
				break;

			case 11:
				cond = "LT";
				break;

			case 12:
				cond = "GT";
				break;

			case 13:
				cond = "LE";
				break;

			default:
				throw new IllegalStateException("CB rt value should be less than 16");
		}
		outputFile.write(String.format("%s %d\n", "B." + cond, COND_BR_Address));
		System.out.println(String.format("%s %d\n", "B." + cond, COND_BR_Address));

	}

	public static void readInstructionsFromFile(String filePath)
	{
		File instructionFile = new File(filePath);
		try
		{
			byte[] buffer = new byte[(int)instructionFile.length()];
			FileInputStream inputStream = new FileInputStream(instructionFile);

			int nRead = 0;

			String s = "";
			while((nRead = inputStream.read(buffer)) != -1)
			{
				for (int i = 0; i < nRead; i++)
				{
					String bin = Integer.toBinaryString(0xFF & buffer[i] | 0x100).substring(1);
					s += bin;

					if(s.length() == 32)
					{
						instructions.add(Long.parseLong(s,2));
						s = "";
					}
				}
			}
			inputStream.close();
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("File not found ra pukka.");
		}

		catch(IOException ex)
		{
			ex.printStackTrace();
		}

	}
}