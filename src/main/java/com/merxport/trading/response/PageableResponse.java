package com.merxport.trading.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageableResponse implements Serializable
{
    private List<?> responseBody;
    private long currentPage;
    private long totalItems;
    private long totalPages;
}
