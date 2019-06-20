package com.scheduler.firstSetting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> selectedUniversity = new MutableLiveData<>();
    private final MutableLiveData<String> selectedDepartment = new MutableLiveData<>();


    public void selectUniversity(String item) {
        selectedUniversity.setValue(item);
    }

    public void selectDepartment(String item) { selectedDepartment.setValue(item); }

    public LiveData<String> getSelectedUniversity() {
        return selectedUniversity;
    }

    public LiveData<String> getSelectedDepartment() { return selectedDepartment; }

}
