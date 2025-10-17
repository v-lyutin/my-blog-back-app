package com.amit.myblog.application.common;

import com.amit.myblog.common.util.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageTest {

    @ParameterizedTest(name = "[{index}] total={0}, pageSize={1} -> lastPage={2}")
    @CsvSource({
            "0, 10, 1",
            "1, 10, 1",
            "9, 10, 1",
            "10, 10, 1",
            "11, 10, 2",
            "100, 10, 10",
            "101, 10, 11",
            "25,  7,  4"
    })
    @DisplayName(value = "Should correctly compute lastPage as ceil(total / pageSize) with minimum value 1")
    void lastPage_shouldComputeLastPageCorrectly(long total, int pageSize, int expectedLastPage) {
        Page<Integer> page = new Page<>(List.of(1, 2), 1, pageSize, total);
        assertEquals(expectedLastPage, page.lastPage());
    }

    @Test
    @DisplayName(value = "Should set hasPrev to true only when current pageNumber is greater than 1")
    void hasPrev_shouldSetHasPrevToTrueOnlyWhenPageNumberGreaterThanOne() {
        Page<Integer> page1 = new Page<>(List.of(), 1, 10, 100);
        Page<Integer> page2 = new Page<>(List.of(), 2, 10, 100);

        assertFalse(page1.hasPrev());
        assertTrue(page2.hasPrev());
    }

    @Test
    @DisplayName(value = "Should set hasNext to true only when current pageNumber is less than lastPag")
    void hasNext_shouldSetHasNextToTrueOnlyWhenPageNumberLessThanLastPage() {
        Page<Integer> page1 = new Page<>(List.of(), 1, 10, 30);
        Page<Integer> page2 = new Page<>(List.of(), 2, 10, 30);
        Page<Integer> page3 = new Page<>(List.of(), 3, 10, 30);

        assertTrue(page1.hasNext());
        assertTrue(page2.hasNext());
        assertFalse(page3.hasNext());
    }

    @Test
    @DisplayName(value = "Should keep items unchanged as provided in constructor")
    void items_shouldKeepItemsUnchangedAsProvidedInConstructor() {
        List<String> items = List.of("a", "b", "c");
        Page<String> page = new Page<>(items, 1, 10, 3);

        assertSame(items, page.items());
        assertEquals(List.of("a", "b", "c"), page.items());
    }

}