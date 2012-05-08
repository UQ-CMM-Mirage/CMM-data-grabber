package au.edu.uq.cmm.paul.servlet;

import java.util.Map;

public class ValidationResult <T> {
    private final Map<String, String> diags;
    private final T target;
    
    public ValidationResult(Map<String, String> diags, T target) {
        super();
        this.diags = diags;
        this.target = target;
    }

    public Map<String, String> getDiags() {
        return diags;
    }

    public T getTarget() {
        return target;
    }

    public boolean isValid() {
        return diags.isEmpty();
    }
}
