import java.util.*;

public class InstructionFinder {
	private Map<Integer, Instruction> instructions;

	public InstructionFinder() {
		instructions = new HashMap<>();
	}

	public void addInstruction(String name, int start, int end, String type) {
		instructions.put(start, new Instruction(name, start, end, type));
	}

	public Instruction findInstruction(int number) {
		for (int start : instructions.keySet()) {
			Instruction instruction = instructions.get(start);
			if (number == instruction.getStart()) {
				return instruction;
			} else if (number >= instruction.getStart() && number <= instruction.getEnd()) {
				return instruction;
			}
		}
		return null;
	}

	// public static void main(String[] args) {
	// InstructionFinder finder = new InstructionFinder();
	// finder.addInstruction("Instruction 1", 10, 20);
	//     finder.addInstruction("Instruction 2", 25, 35);
	//     Instruction instruction = finder.findInstruction(15);
	//     if (instruction != null) {
	//         System.out.println("Instruction found: " + instruction.getName());
	//     } else {
	//         System.out.println("Instruction not found.");
	//     }
	// }
}

class Instruction {
	private String name;
	private int start;
	private int end;
	private String type;

	public Instruction(String name, int start, int end, String type) {
		this.name = name;
		this.start = start;
		this.end = end;
		this.type = type;
	}

	public String getType() {
		return type;
	}
	public String getName() {
		return name;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return "Instruction{" +
				"name='" + name + '\'' +
				", start=" + start +
				", end=" + end +
				", type='" + type + '\'' +
				'}';
	}
}