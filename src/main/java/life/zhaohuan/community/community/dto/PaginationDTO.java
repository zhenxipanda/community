package life.zhaohuan.community.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 需求：让每个Question 也显示页码等信息
 */
@Data
public class PaginationDTO<T> {  //这个对象包裹的就是页面承载的元素,传到前端去
    private List<T> data;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private  Integer page;
    private  List<Integer> pages = new ArrayList<>(); //包含的页列表
    private  Integer totalPage;

    public void setPagination(Integer totalPage, Integer page) {
        this.totalPage = totalPage;
        this.page = page;
        pages.add(page);
        // page 是第几页，如果 page = 3, 上一步先把 3 放进去，然后放 2,1,0
        for(int i = 1;i <= 3 ;i ++){
            if(page - i > 0){
                pages.add(0 , page - i);
            }
            if(page + i <= totalPage){ // 保证如果小于等于尾页，向后展示3页
                pages.add(page + i);
            }
        }

        //是否展示上一页 <
        // 当为第一页的时候，不展示前一页
        if(page == 1){
            showPrevious = false;
        }
        else{
            showPrevious = true;
        }
        //是否展示下一页  >
        // 当为最后一页的时候，不展示后一页
        if(page == totalPage){
            showNext = false;
        }
        else{
            showNext = true;
        }

        // 是否展示第一页   <<
        // 当列表里不包含第一页，就展示第一页，包含就不展示
        if(pages.contains(1)){
            showFirstPage = false;
        }else{
            showFirstPage = true;
        }

        // 是否展示最后一页   >>
        // 当列表里不包含最后一页时，展示，包含，不展示
        if(pages.contains(totalPage)){
            showEndPage = false;
        }
        else{
            showEndPage = true;
        }
    }
}
