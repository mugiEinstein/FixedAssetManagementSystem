package ztt.fixedassetmanagement.common;

import lombok.Data;

@Data
public class PageParams {
    private int page = 1;
    private int pageSize = 10;

    public int getOffset() {
        return (page - 1) * pageSize;
    }
}