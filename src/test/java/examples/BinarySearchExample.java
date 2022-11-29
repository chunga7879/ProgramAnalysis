package examples;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;

public class BinarySearchExample {
    int binarySearchWithoutAnnotations(int[] nums, int search, int low, int high) {
        while (low != high) {
            int mid = (low + high) / 2;
            int midNumber = nums[mid];
            if (search == midNumber) {
                return mid;
            } else if (search > midNumber) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    int binarySearchWithAnnotations(@NotEmpty int[] nums, int search, @PositiveOrZero int low, @PositiveOrZero int high) {
        while (low != high) {
            int mid = (low + high) / 2;
            int midNumber = nums[mid];
            if (search == midNumber) {
                return mid;
            } else if (search > midNumber) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

}