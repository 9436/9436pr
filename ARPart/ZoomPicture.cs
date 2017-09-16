using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ZoomPicture : MonoBehaviour
{
    public GameObject ZoominCell;
    private Sprite ImageRef;
    void Start()
    {
        ImageRef = gameObject.GetComponent<Image>().sprite;

    }
    public void SizeUpPicture()
    {
        var New = Instantiate<GameObject>(ZoominCell);
        // 버튼 클릭시 UI에다 큰 이미지를 생성해준다.
        New.GetComponent<Image>().sprite = ImageRef;
        New.transform.SetParent(gameObject.transform.parent);
        New.GetComponent<RectTransform>().anchoredPosition = new Vector2(0, 0);
        //그리고 그 UI을 다시 클릭하면 사라진다.

    }
    public void Delme()
    {
        DestroyImmediate(gameObject);
    }
}
