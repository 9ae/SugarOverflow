package me.valour.sugaroverflow.service;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.mock.MockRequest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import me.valour.sugaroverflow.model.Question;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by alice on 4/1/16.
 */

@RunWith(MockitoJUnitRunner.class)
public class StackExchangeAPITest {

    @Mock
    Context mMockContext;

    @Mock
    Volley MockVolley;

    @Test
    public void isSingleton(){

        RequestQueue mockRequestQueue = mock(RequestQueue.class);

        when(MockVolley.newRequestQueue(mMockContext)).thenReturn(mockRequestQueue);

        StackExchangeAPI api1 = StackExchangeAPI.getInstance(mMockContext);
        StackExchangeAPI api2 = StackExchangeAPI.getInstance(mMockContext);

        assertEquals(api1, api2);

    }


}
