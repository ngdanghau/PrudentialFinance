package com.example.prudentialfinance.Activities.General;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.prudentialfinance.Helpers.Alert;
import com.example.prudentialfinance.Helpers.LoadingDialog;
import com.example.prudentialfinance.Model.Category;
import com.example.prudentialfinance.Model.GlobalVariable;
import com.example.prudentialfinance.Model.Goal;
import com.example.prudentialfinance.Model.SiteSettings;
import com.example.prudentialfinance.Model.User;
import com.example.prudentialfinance.R;
import com.example.prudentialfinance.RecycleViewAdapter.GoalRecycleViewAdapter;
import com.example.prudentialfinance.ViewModel.Goal.GoalViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class GoalActivity extends AppCompatActivity {

    GoalViewModel viewModel;
    ImageButton Btn_back, Btn_add, Btn_more;
    LoadingDialog loadingDialog;
    Alert alert;
    Map<String, String > headers;
    ArrayList<Goal> data;
    GoalRecycleViewAdapter adapter;
    RecyclerView rViewGoal;
    LinearLayoutManager manager;
    SwipeRefreshLayout swipeRefreshLayout;

    Goal entry;
    SiteSettings appInfo;
    User authUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        setControl();
        loadComponent();
        setEvent();
        loadData();
        setSwipe();
    }

    private void setControl(){
        Btn_back = findViewById(R.id.Btn_back);
        Btn_add = findViewById(R.id.Btn_AddGoal);
        Btn_more = findViewById(R.id.Btn_more);
        rViewGoal = findViewById(R.id.rv_Goals);
        swipeRefreshLayout = findViewById(R.id.refreshLayoutGoal);

    }
    @SuppressLint("NotifyDataSetChanged")

    private void setEvent(){
        Btn_back.setOnClickListener(view -> finish());

        Btn_add.setOnClickListener(view ->{
            Intent intent = new Intent (this,AddGoalActivity.class);
            intent.putExtra("goal", new Goal(0));
            addGoalActivity.launch(intent);
        });

        alert.btnOK.setOnClickListener(view -> alert.dismiss());

        viewModel.isLoading().observe(this, isLoading -> {
            if(isLoading){
                loadingDialog.startLoadingDialog();
            }else{
                loadingDialog.dismissDialog();
            }
        });

        viewModel.getObject().observe( this, object -> {
            if(object == null){
                alert.showAlert(getResources().getString(R.string.alertTitle), getResources().getString(R.string.alertDefault), R.drawable.ic_close);
                return;
            }

            if (object.getResult() == 1) {
                data.clear();
                data.addAll(object.getData());
                adapter.notifyDataSetChanged();
            } else {
                alert.showAlert(getResources().getString(R.string.alertTitle), object.getMsg(), R.drawable.ic_close);
            }
        });

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Lọc");
        String[] types = {"Chưa hoàn thành", "Hoàn thành","Quá hạn"};
        b.setItems(types, (dialog, which) -> {
            dialog.dismiss();
            switch(which){
                case 0:
                    data.clear();
                    viewModel.getData(headers, "",1);
                    break;
                case 1:
                    data.clear();
                    viewModel.getData(headers, "",2);
                    break;
                case 2:
                    data.clear();
                    viewModel.getData(headers, "",3);
                    break;
            }
        });

        Btn_more.setOnClickListener(view -> {
              b.show();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            data.clear();
            viewModel.getData(headers, "",0);
            swipeRefreshLayout.setRefreshing(false);
        });
    }


    private void loadData() {
        data = new ArrayList<>();
        viewModel.getData(headers, "",0);

        manager = new LinearLayoutManager(this);
        rViewGoal.setLayoutManager(manager);

        adapter = new GoalRecycleViewAdapter(this, data, addGoalActivity, appInfo);
        rViewGoal.setAdapter(adapter);

    }

    private void loadComponent() {
        headers = ((GlobalVariable)getApplication()).getHeaders();
        authUser = ((GlobalVariable)getApplication()).getAuthUser();
        appInfo = ((GlobalVariable)getApplication()).getAppInfo();
        loadingDialog = new LoadingDialog(GoalActivity.this);
        alert = new Alert(this, 1);
        viewModel = new ViewModelProvider(this).get(GoalViewModel.class);
    }

    private void setSwipe() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Take action for the swiped item
                int position = viewHolder.getLayoutPosition();
                entry = data.get(position);

                data.remove(position);
                adapter.notifyItemRemoved(position);


                Snackbar.make(rViewGoal, "Đã xóa " + entry.getName(), 10000)
                        .addCallback(new Snackbar.Callback(){
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                    viewModel.deteteItem(headers, entry.getId());
                                }
                            }
                        })
                        .setAction("Khôi phục", view -> {
                            data.add(position, entry);
                            adapter.notifyItemInserted(position);
                        }).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(GoalActivity.this, R.color.colorRed))
                        .addActionIcon(R.drawable.ic_close)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rViewGoal);
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> addGoalActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == 78) {
                    // There are no request codes
                    Intent data = result.getData();
                    assert data != null;
                    Goal dataFromActivity = (Goal) data.getSerializableExtra("goal_entry");

                    System.out.println(dataFromActivity.toString());
                    addData(dataFromActivity);
                }else  if (result.getResultCode() == 79) {
                    // There are no request codes
                    Intent data = result.getData();
                    assert data != null;
                    int id = (int) data.getSerializableExtra("id");
                    int deposit = (int) data.getSerializableExtra("deposit");

                    System.out.println("DEPOSIT : ID= "+id);
                    deposit(id, deposit);
                }
            });



    public void addData(Goal entry){
        boolean isAdd = true;
        for (Goal item: data) {
            if(item.getId()==entry.getId()){
                item.setAmount(entry.getAmount());
                item.setBalance(entry.getBalance());
                item.setName(entry.getName());
                item.setDeadline(entry.getDeadline());
                isAdd = false;
                break;
            }
        }

        if(isAdd){
            data.add(0, entry);
        }
        adapter.notifyDataSetChanged();
    }


    public void deposit(int id,int deposit){
        for (Goal item: data) {
            if(item.getId()==id){
               item.setDeposit(item.getDeposit()+deposit);
            }
        }
        adapter.notifyDataSetChanged();
    }
}