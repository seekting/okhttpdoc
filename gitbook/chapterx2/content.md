# OkHttp-IO Segment及SegmentPool

Segment是拥有一个byte数组的单元，翻译为分段，okhttp里的数据读写都是放在多个segment里，
而segment可以看成一个节点，它有前节点和后节点。多个Segment组成了一个链式存储模式

```java
 final class Segment {
   static final int SIZE = 8192;

   static final int SHARE_MINIMUM = 1024;

   final byte[] data;

   int pos;

    int limit;

   boolean shared;

   boolean owner;

   Segment next;

   Segment prev;
   }

```

## SIZE
表示默认的byte数组大小,在构造函数中会开辟SIZE大小的byte数组
```java
  public Segment() {
    this.data = new byte[SIZE];
    this.owner = true;
    this.shared = false;
  }

```
## next,prev
左节点，右节点，双向链表的数据结构

## pos limit
pos是position的缩写，如果了解ByteBuffer就理解了。<br>
假如有这么一个byte数组[1,2,3,4,5,6,7,8,9]<br>
如果pos=4,limit=8,那么这个segment的有效部分就是[5,6,7,8,9]；其它的无效，这样的好处是不要频繁去new数组
## shared,owner
shared 如果自己的byte[]数组用的是别人的，shared=true<br>
owner 如果自己byte[]数组用的是自己的，owner=true<br>
当split一个Segment的时候，如果byteCount大于SHARE_MINIMUM，则会分裂一个新的Segment，但是这个新Segment用的是源Segment的byte[]。
这个时候新Segment的shared=true,owner=false,表示它用的是别人的data。<br>
在writeTo和compact的操作里会用到这两个值
## push

## shared和SHARE_MINIMUM及split()
判断要不要共享数据的临界点，split的时候会用到。<br>
当split segment的时候，如果要split的长度大于SHARE_MINIMUM，得到的新segment的byte数组和源segment是同一个，得出的新segment会标记为shared=true
```java
 public Segment split(int byteCount) {
 //...ignore code
    Segment prefix;
    if (byteCount >= SHARE_MINIMUM) {
    //如果byteCount大于1024就和源segment用一个byte[]
      prefix = new Segment(this);
    } else {
      prefix = SegmentPool.take();//从池子里拿
      //把数据里的pos到pos+byteCount考到新数组的0到byteCount里
      System.arraycopy(data, pos, prefix.data, 0, byteCount);
    }

    prefix.limit = prefix.pos + byteCount;
    pos += byteCount;
    //分裂后，这个segment的实际大小变小了
    //以前是pos到limit，现在是pos+byteCount到limit
    prev.push(prefix);//在它和它的前一节点中间加上新一个节点
    return prefix;
  }

```

split方法把自己从pos->limit里的第pos->pos+byteCount的数据分裂出来，加到链表里<br>
假如有这个一个Segment链表[123]-[456]-[789]<br>
想对[456]这个Segment分裂，得到的结果会是[123]-*[4]-[56]-[789]<br>



