package edu.nchu.mall.models.callback;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import edu.nchu.mall.models.model.CallbackBody;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BrandCallback extends CallbackBody {

    private String name;

    private String descript;

    private Integer showStatus;

    private String firstLetter;

    private Integer sort;
}
