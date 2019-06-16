package com.scheduler.firstSetting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> selectedUniversity = new MutableLiveData<>();
    private final MutableLiveData<String> selectedDepartment = new MutableLiveData<>();


    void selectUniversity(String item) {
        selectedUniversity.setValue(item);
    }

    void selectDepartment(String item) { selectedDepartment.setValue(item); }

    LiveData<String> getSelectedUniversity() {
        return selectedUniversity;
    }

    LiveData<String> getSelectedDepartment() { return selectedDepartment; }

}
