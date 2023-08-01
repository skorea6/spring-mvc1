package hello.servlet.web.frontcontroller;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ModelView {
    private String viewName;
    private Map<String, Object> model = new HashMap<>();

    public ModelView(String viewName) {
        this.viewName = viewName;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}
