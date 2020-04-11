package bowling.domain;

import bowling.domain.exception.OutOfRangeArgumentException;

public class FinalFrame implements Frame {
    private static final int MAX_PIN_COUNT_SIZE = 3;
    private static final int MIN_PIN_COUNT_FOR_THIRD = 10;
    private static final int MAX_PIN_COUNT = 30;
    private static final String OUT_OF_RANGE_ERROR_MESSAGE =
            "잘못된 핀 카운트 입니다.";

    private PinCounts pinCounts;

    public FinalFrame() {
        pinCounts = new PinCounts(MAX_PIN_COUNT_SIZE);
    }

    @Override public void addPinCount(PinCount pinCount) {
        if (!isAddable(pinCount)) {
            throw new OutOfRangeArgumentException(OUT_OF_RANGE_ERROR_MESSAGE);
        }

        if (!isDone()) {
            pinCounts.add(pinCount);
        }
    }

    private boolean isAddable(PinCount pinCount) {
        if (pinCounts.size() != 1) {
            return true;
        }
        PinCount firstPinCount = pinCounts.getFirst()
                .orElse(PinCount.empty());

        if(firstPinCount.isMax()) {
            return true;
        }
        return firstPinCount.isOverMaxAfterAdd(pinCount);
    }

    @Override public int getScore() {
        return pinCounts.getPintCountTotal();
    }

    @Override public boolean isDone() {
        if (pinCounts.isFull()) {
            return true;
        }
        return pinCounts.size() == 2 &&
                pinCounts.getPintCountTotal() < MIN_PIN_COUNT_FOR_THIRD;
    }
}
