package edu.nchu.mall.models.dto.mq;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockLocked {
    @NotNull
    private Long taskId;
    @NotNull
    private List<Long> detailIds;
}
