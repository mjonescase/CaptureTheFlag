package com.michaelwilliamjones.dynamicfragmenttest;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;
    private boolean isViewCreated = false;
    private Queue<String> messageBacklog;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment HomeFragment.
     */
    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        fragment.setArguments(new Bundle());
        MyAdapter adapter = new MyAdapter(new ArrayList<String>());
        fragment.setAdapter(adapter);
        fragment.setMessageBacklog(new LinkedList<>());
        return fragment;
    }

    public void setAdapter(MyAdapter adapter) {
        mAdapter = adapter;
    }

    public void setMessageBacklog(Queue queue) {
        messageBacklog = queue;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        isViewCreated = true;
        clearMessageBacklog();
    }

    public void addNewMessage(String message) {
        messageBacklog.add(message);
        if(isViewCreated) {
            clearMessageBacklog();
        }
    }

    public void clearMessageBacklog() {
        if(messageBacklog == null ) {
            return;
        }
        
        String message = messageBacklog.poll();
        while(message != null) {
            mAdapter.addItem(message);
            mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
            message = messageBacklog.poll();
        }
    }
}
