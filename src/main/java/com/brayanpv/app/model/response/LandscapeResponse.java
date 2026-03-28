package com.brayanpv.app.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LandscapeResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String status;
}
