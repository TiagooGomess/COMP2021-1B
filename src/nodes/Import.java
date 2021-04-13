package nodes;

import java.util.Arrays;
import java.util.List;

public class Import {
    private String pathToClass;
    private String className;

    public Import(String importString) {
        List<String> steps = Arrays.asList(importString.split("\\."));
        int size = steps.size();
        this.className = steps.get(size - 1);
        this.pathToClass = String.join(".", steps.subList(0, size - 1));
    }
}
