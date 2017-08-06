package bl.taxi.rider.models.placeautocomplete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Term {

    @SerializedName("offset")
    @Expose
    private Long offset;
    @SerializedName("value")
    @Expose
    private String value;

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}