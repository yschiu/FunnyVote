package com.android.heaton.funnyvote.ui.votedetail;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.heaton.funnyvote.R;
import com.android.heaton.funnyvote.database.Option;
import com.android.heaton.funnyvote.eventbus.EventBusController;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.android.heaton.funnyvote.eventbus.EventBusController.OptionChoiceEvent.EVENT_CHOICED;

/**
 * Created by heaton on 2016/9/2.
 */

public class VHUnpollCreateOptionItem extends RecyclerView.ViewHolder {

    @BindView(R.id.imgAdd)
    ImageView imgAdd;
    @BindView(R.id.relAdd)
    RelativeLayout relAdd;
    @BindView(R.id.txtOptionNumber)
    TextView txtOptionNumber;
    @BindView(R.id.imgChoice)
    ImageView imgChoice;
    @BindView(R.id.edtOptionTitle)
    EditText edtOptionTitle;
    @BindView(R.id.imgDelete)
    ImageView imgDelete;
    @BindView(R.id.relNormal)
    RelativeLayout relNormal;
    private boolean isMulti;
    private boolean isChoice;

    public VHUnpollCreateOptionItem(View itemView, boolean isMulti) {
        super(itemView);
        this.isMulti = isMulti;
        ButterKnife.bind(this, itemView);
    }

    public void setLayout(boolean isChoice, Option option) {
        this.isChoice = isChoice;
        txtOptionNumber.setText(Integer.toString(getAdapterPosition() + 1));
        if (getItemViewType() == OptionItemAdapter.OPTION_UNPOLL_VIEW_TYPE_ADD_NEW) {
            relNormal.setVisibility(View.INVISIBLE);
            relAdd.setVisibility(View.VISIBLE);
            imgChoice.setVisibility(View.GONE);
            imgDelete.setVisibility(View.GONE);
        } else if (getItemViewType() == OptionItemAdapter.OPTION_UNPOLL_VIEW_TYPE_INPUT_CONTENT) {
            relNormal.setVisibility(View.VISIBLE);
            relAdd.setVisibility(View.INVISIBLE);
            imgChoice.setVisibility(View.VISIBLE);
            imgDelete.setVisibility(View.VISIBLE);
            edtOptionTitle.setText(option.getTitle());
            edtOptionTitle.addTextChangedListener(new optionEditTextListener());
            setUpImgChoiceLaout();
        }
    }


    private void setUpImgChoiceLaout() {
        if (!isMulti) {
            imgChoice.setImageResource(isChoice ? R.drawable.ic_radio_button_checked_40dp
                    : R.drawable.ic_radio_button_unchecked_40dp);
        } else {
            imgChoice.setImageResource(isChoice ? R.drawable.ic_check_box_40dp
                    : R.drawable.ic_check_box_outline_blank_40dp);
        }
    }

    @OnClick(R.id.relAdd)
    public void addNewOption() {
        EventBus.getDefault().post(new EventBusController.OptionControlEvent(getAdapterPosition(), null, EventBusController.OptionControlEvent.OPTION_ADD));
    }

    @OnClick(R.id.imgDelete)
    public void removeOption() {
        EventBus.getDefault().post(new EventBusController
                .OptionControlEvent(getAdapterPosition(), null, EventBusController.OptionControlEvent.OPTION_REMOVE));
    }

    @OnClick(R.id.imgChoice)
    public void onOptionChoice() {
        setUpImgChoiceLaout();
        EventBus.getDefault().post(new EventBusController
                .OptionChoiceEvent(getAdapterPosition(), EVENT_CHOICED));
    }

    private class optionEditTextListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EventBus.getDefault().post(new EventBusController
                    .OptionControlEvent(getAdapterPosition(), s.toString(), EventBusController.OptionControlEvent.OPTION_INPUT_TEXT));

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
