package com.example.ase;

import com.Data.MyServerData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;


@RunWith(Parameterized.class)
public class getFirstQuestionNumberFromCategoryTest_withDummyData {
    @Parameterized.Parameters(name = "Params :{0}-{1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"SecondCategory",3}, {"FirstCategory",1}
        });
    }
    @Parameterized.Parameter
    public String myInput;
    @Parameterized.Parameter(value = 1)
    public int myExpected;

    @Test
    public void getQuestionByNumber_returnsQuestion(){
        assertEquals(MyServerData.getInstance().getFirstQuestionNumberFromCategory(myInput), myExpected);
    }

}

