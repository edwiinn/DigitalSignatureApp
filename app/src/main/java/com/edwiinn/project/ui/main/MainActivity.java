package com.edwiinn.project.ui.main;

import android.os.Bundle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;

import com.edwiinn.project.R;
import com.edwiinn.project.ui.base.BaseActivity;
import com.edwiinn.project.ui.documents.DocumentsFragment;
import com.edwiinn.project.ui.signature.SignatureFragment;

public class MainActivity extends BaseActivity implements MainMvpView, BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    MainPresenter<MainMvpView> mPresenter;

    @BindView(R.id.btm_nav)
    BottomNavigationView bottomNavigationView;

    SparseArray<Fragment.SavedState> fragmentState;

    Integer currentSelectedItemId;

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(MainActivity.this);
        setUp();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    @Override
    protected void setUp() {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        fragmentState = new SparseArray<>();
        swapFragments(new DocumentsFragment(), R.id.action_documents);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_documents:
                swapFragments(new DocumentsFragment(), item.getItemId());
                return true;
            case R.id.action_signature:
                swapFragments(new SignatureFragment(), item.getItemId());
                return true;
        }
        return false;
    }

    private void swapFragments(Fragment fragment, int itemId) {
        if (getSupportFragmentManager().findFragmentById(itemId) == null) {
            saveFragmentState(itemId);
            createFragment(fragment, itemId);
        }
    }

    private void saveFragmentState(int itemId) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null) {
            Fragment.SavedState state = getSupportFragmentManager().saveFragmentInstanceState(currentFragment);
            fragmentState.put(currentSelectedItemId, state);
        }
        currentSelectedItemId = itemId;
    }

    private void createFragment(Fragment fragment, int itemId) {
        fragment.setInitialSavedState(fragmentState.get(itemId));

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}