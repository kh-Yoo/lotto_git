package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    // 🎯 HistoryActivity에서 실시간으로 업데이트하는 바로 그 리스트를 참조합니다.
    private final List<LottoEntity> lottoHistoryList;

    public HistoryAdapter(List<LottoEntity> lottoHistoryList) {
        this.lottoHistoryList = lottoHistoryList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lotto_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        LottoEntity currentItem = lottoHistoryList.get(position);
        holder.bind(currentItem);
    }

    @Override
    public int getItemCount() {
        return lottoHistoryList.size();
    }

    // 🎯 (선택 사항) 리스트 전체를 갱신해야 할 경우 사용하지만, 실시간 업데이트에서는 notifyItemInserted(0)를 사용하므로 자주 쓰이지 않습니다.
    public void setLottoHistoryList(List<LottoEntity> newList) {
        lottoHistoryList.clear();
        lottoHistoryList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView textDrwNo;
        private final TextView ball1, ball2, ball3, ball4, ball5, ball6;
        private final TextView ballBonus;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            // item_lotto_history.xml의 View ID와 연결
            textDrwNo = itemView.findViewById(R.id.textDrwNo);

            ball1 = itemView.findViewById(R.id.ball1);
            ball2 = itemView.findViewById(R.id.ball2);
            ball3 = itemView.findViewById(R.id.ball3);
            ball4 = itemView.findViewById(R.id.ball4);
            ball5 = itemView.findViewById(R.id.ball5);
            ball6 = itemView.findViewById(R.id.ball6);
            ballBonus = itemView.findViewById(R.id.ballBonus);
        }

        public void bind(LottoEntity entity) {
            // 1. 회차 및 날짜 정보
            String drwInfo = String.format("제 %d회 (%s)", entity.drwNo, entity.drwNoDate);
            textDrwNo.setText(drwInfo);

            // 2. 당첨 번호 설정
            ball1.setText(String.valueOf(entity.drwtNo1));
            ball2.setText(String.valueOf(entity.drwtNo2));
            ball3.setText(String.valueOf(entity.drwtNo3));
            ball4.setText(String.valueOf(entity.drwtNo4));
            ball5.setText(String.valueOf(entity.drwtNo5));
            ball6.setText(String.valueOf(entity.drwtNo6));

            // 3. 보너스 번호 설정
            ballBonus.setText(String.valueOf(entity.bnusNo));

            // TODO: (선택 사항) 번호에 따라 공의 색상을 변경하는 로직을 여기에 추가할 수 있습니다.
            // setBallColor(ball1, entity.drwtNo1);
            // ...
        }
    }
}