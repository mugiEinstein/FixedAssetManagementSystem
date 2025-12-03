package ztt.fixedassetmanagement.common;

import lombok.Data;
import lombok. NoArgsConstructor;
import lombok. AllArgsConstructor;
import java.util. List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> list;
    private long total;
    private int page;
    private int pageSize;

    public static <T> PageResult<T> of(List<T> list, long total, int page, int pageSize) {
        return new PageResult<>(list, total, page, pageSize);
    }
}