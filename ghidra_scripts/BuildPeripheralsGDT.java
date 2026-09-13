// Script to rebuild data/typeinfo/ac7911b8_peripherals.gdt from data/headers/ac7911b8_peripherals.h
//@category JieLi
//@keybinding
//@menupath Tools.JieLi.Rebuild AC7911B8 Peripherals GDT
//@toolbar

import ghidra.app.script.GhidraScript;
import ghidra.app.util.cparser.C.CParserUtils;
import ghidra.program.model.data.DataTypeManager;
import ghidra.program.model.data.FileDataTypeManager;
import java.io.File;

public class BuildPeripheralsGDT extends GhidraScript {

    @Override
    public void run() throws Exception {
        File headerFile = new File("ghidra-jieli/data/headers/ac7911b8_peripherals.h");
        if (!headerFile.exists()) {
            // Check relative to current working directory or repo root
            headerFile = new File("data/headers/ac7911b8_peripherals.h");
        }
        if (!headerFile.exists()) {
            printerr("Cannot find header file: " + headerFile.getAbsolutePath());
            return;
        }

        File gdtFile = new File(headerFile.getParentFile().getParentFile(), "typeinfo/ac7911b8_peripherals.gdt");
        gdtFile.getParentFile().mkdirs();
        if (gdtFile.exists()) {
            gdtFile.delete();
        }

        println("Compiling " + headerFile.getAbsolutePath() + " -> " + gdtFile.getAbsolutePath() + "...");
        String[] fileNames = new String[] { headerFile.getAbsolutePath() };
        DataTypeManager[] openDTMs = new DataTypeManager[] { currentProgram.getDataTypeManager() };
        String[] args = new String[] { "-D_GHIDRA_" };

        FileDataTypeManager archiveDTM = CParserUtils.parseHeaderFiles(
            openDTMs, fileNames, args, gdtFile.getAbsolutePath(), monitor
        );

        if (archiveDTM != null) {
            try {
                archiveDTM.save();
                println("Successfully built " + gdtFile.getName() + " (" + archiveDTM.getDataTypeCount(true) + " data types).");
            } finally {
                archiveDTM.close();
            }
        } else {
            printerr("Failed to build GDT archive.");
        }
    }
}
