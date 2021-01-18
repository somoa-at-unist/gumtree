package com.github.gumtreediff.gen.jdt;

import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;

public class JdtTreeMapping {
    public JdtTreeMapping() {

    }
    public static void main(String[] args) {
        System.out.println("gumtree main");
        String srcFilename = "org/joda/time/Partial.java";
        String dstFilename = "org/joda/time/Partial.java";
        Path srcFile = FileSystems.getDefault().getPath("../JQF/src/test/resources/patches/Patch180/Time4b/src/main/java",srcFilename);
        Path dstFile = FileSystems.getDefault().getPath("../JQF/src/test/resources/patches/Patch180/Time4p/src/main/java",dstFilename);

        //Path srcFile = FileSystems.getDefault().getPath("gen.jdt","src", "test", "resources", "simple", "Example_v0.java");
        //Path dstFile = FileSystems.getDefault().getPath("gen.jdt","src", "test", "resources", "simple", "Example_v1.java");
        try {
            HashMap<String, Integer> hm = (new JdtTreeMapping()).mapping(srcFile, dstFile, srcFilename);
            for(String str: hm.keySet()) {
                System.out.println(str + "->" + hm.get(str));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public HashMap<String, Integer> mapping(Path srcFile, Path dstFile, String srcFilename) throws IOException {
        File f = (new File(srcFile.toString()));
        if(!f.exists()) {
            System.out.println("does not exist");
            System.out.println(f.getAbsolutePath());
            return null;
        }
        JdtTreeContext srcTreeCtxt = (JdtTreeContext) new JdtTreeGenerator().generateFrom().file(srcFile);
        JdtTreeContext dstTreeCtxt = (JdtTreeContext) new JdtTreeGenerator().generateFrom().file(dstFile);

        final Tree srcRoot = srcTreeCtxt.getRoot();
        final Tree dstRoot = dstTreeCtxt.getRoot();

        final CompilationUnit srcRootASTNode = (CompilationUnit) srcTreeCtxt.getRootNode();
        final CompilationUnit dstRootASTNode = (CompilationUnit) dstTreeCtxt.getRootNode();

        Matcher defaultMatcher = Matchers.getInstance().getMatcher();
        final MappingStore mappings = defaultMatcher.match(srcRoot, dstRoot);
        //System.out.println(mappings.toString());
        HashMap<String, Integer> lineMapping = new HashMap<>();
        TreeVisitor.visitTree(srcRoot, new TreeVisitor(){
            @Override
            public void startTree(Tree tree) {
                int srcLine = srcRootASTNode.getLineNumber(tree.getPos());
                Tree matching = mappings.getDstForSrc(tree);
                if(matching != null) {
                    //System.out.println("tree: " + tree.toString() + " -> " + matching.toString());
                    int dstLine = dstRootASTNode.getLineNumber(matching.getPos());
                    //System.out.println(srcLine + " : " + dstLine);
                    String key = srcFilename + ":" + srcLine;
                    lineMapping.put(key, dstLine);
                }
            }

            @Override
            public void endTree(Tree tree) {
            }
        });
        return lineMapping;
    }
}