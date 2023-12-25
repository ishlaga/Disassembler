# Project 2

For this assignment, you will be implementing a disassembler for the binaries that run on our LEGv8 emulator in binary mode.  Your disassembler will handle input files containing any number of contiguous, binary LEGv8 instructions encoded in big-endian byte order.  The input file name will be given as the first command line parameter.  Your output, printed to the terminal, should be--modulo some caveats discussed below--the original LEGv8 assembly code that generated the binary.

Except that it ignores the PC and flow control, a disassembler essentially implements the first two stages (fetch and decode) of the five-stage pipeline described in lecture and the textbook.  A working disassembler requires perhaps half of the total work of building a binary emulator.

Your disassembler should fully support the following set of LEGv8 instructions:

```
ADD
ADDI
AND
ANDI
B
B.cond: This is a CB instruction in which the Rt field is not a register, but a code that indicates the condition extension. These have the values (base 16):
    0: EQ
    1: NE
    2: HS
    3: LO
    4: MI
    5: PL
    6: VS
    7: VC
    8: HI
    9: LS
    a: GE
    b: LT
    c: GT
    d: LE
BL
BR: The branch target is encoded in the Rn field.
CBNZ
CBZ
EOR
EORI
LDUR
LSL: This instruction uses the shamt field to encode the shift amount, while Rm is unused.
LSR: Same as LSL.
ORR
ORRI
STUR
SUB
SUBI
SUBIS
SUBS
MUL
PRNT: This is an added instruction (part of our emulator, but not part of LEG or ARM) that prints a register name and its contents in hex and decimal.  This is an R instruction.  The opcode is 11111111101.  The register is given in the Rd field.
PRNL: This is an added instruction that prints a blank line.  This is an R instruction.  The opcode is 11111111100.
DUMP: This is an added instruction that displays the contents of all registers and memory, as well as the disassembled program.  This is an R instruction.  The opcode is 11111111110.
HALT: This is an added instruction that triggers a DUMP and terminates the emulator.  This is an R instruction.  The opcode is 11111111111
```


How to Execute:
1. Call "bash Build.sh": this will build our program by calling the java compiler for all *.java files.

2. Call "bash Run.sh filename.legv8asm.machine" with the given file name: this will just run our program with the input of the "filename.legv8asm.machine" file inputted.
2a. If you'd like it to be printed into a file rather than printed to the console, call "bash Run.sh filename.legv8asm.machine >> output.txt" instead!

3. Once you are finished, call "bash Clean.sh" for a quick cleanup of all the .class files. This doesn't clean everything, it only deletes all the .class files so you may compiler it again if need be.

How it Works:
- Overall: The program here takes in 4 bytes (using a FileInputStream, very handy) at a time from the .machine file, and immediately calls analyzeInstruction them them to put them into an array list of all of our instructions. Once we finish analyzing all the instructions we look for branch targets that were used by all B and CB type calls, and we remember where they are going to go to. Once we iterate through all of that, we add them into our array list right before our final call, and we only add in the ones that were actually targeted by B and CB calls. Once we finish, we iterate through our array list and call a toString() for each instruction.
- analyzeInstruction: What our analyzeInstruction does it just compare the OpCode for the current instruction to the instructions we have to support. In general it's a bunch of switch statements for each big kind of instruction type. When it finds the one we're looking for, we add a new operator to our array list of that specific type and continue onto the next instruction in the main.
- operator.java and all subclasses: This is probably the most important thing for us, as we collected a bunch of these classes subclasses to call a .toString onto. This class just defines a method we'd like to use for all the subclasses, and it also additionally acts as a type for our array list.
- toString: the toString method for each of the operator.java classes has been formatted to output the correct String we'd like to print to the console for each type of instruction we have to support.
