package com.project.vetdata.templates;

import com.project.vetdata.dto.DiagnosticCreateDTO;
import com.project.vetdata.dto.PostOperativeCreateDTO;
import com.project.vetdata.model.Diagnostic;
import com.project.vetdata.model.PostOperative;

public final class PostOperativeTemplate {

    public static PostOperativeCreateDTO getFakePostOperativeCreateDTO() {
        return new PostOperativeCreateDTO("Fisioterapia");
    }

    public static PostOperativeCreateDTO getFakePostOperativeCreateDTO2() {
        return new PostOperativeCreateDTO("Repouso");
    }

    public static PostOperative getFakePostOperative() {
        return new PostOperative("Fisioterapia");
    }

    public static PostOperative getFakePostOperative2() {
        return new PostOperative("Repouso");
    }

    public static PostOperativeCreateDTO getFakePostOperativeCreateDTOWithInvalidDescription() {
        return new PostOperativeCreateDTO();
    }
}
