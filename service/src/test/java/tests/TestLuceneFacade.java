package tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.caucho.junit.ConfigurationBaratine;
import com.caucho.junit.RunnerBaratine;
import com.caucho.junit.ServiceTest;
import com.caucho.lucene.LuceneEntry;
import com.caucho.lucene.LuceneFacadeImpl;
import com.caucho.lucene.LuceneFacadeSync;
import com.caucho.lucene.LuceneReaderImpl;
import com.caucho.lucene.LuceneWriterImpl;
import com.caucho.lucene.SearcherUpdateServiceImpl;
import io.baratine.service.Lookup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * title: tests LuceneFacade methods
 */
@RunWith(RunnerBaratine.class)
@ServiceTest(LuceneWriterImpl.class)
@ServiceTest(LuceneReaderImpl.class)
@ServiceTest(LuceneFacadeImpl.class)
@ServiceTest(SearcherUpdateServiceImpl.class)
public class TestLuceneFacade extends Base
{
  @Inject
  @Lookup("public://lucene/service")
  LuceneFacadeSync _lucene;

  @Test
  public void testText()
  {
    _lucene.indexText("foo", "foo", "mary had a little lamb");

    applyChanges();

    List<LuceneEntry> result = _lucene.search("foo", "mary", 255);

    Assert.assertEquals(1, result.size());
    Assert.assertEquals("foo", result.get(0).getExternalId());
  }

  @Test
  public void testDelete()
  {
    _lucene.indexText("foo", "foo", "mary had a little lamb");
    _lucene.delete("foo", "foo");

    applyChanges();

    List<LuceneEntry> result = _lucene.search("foo", "mary", 255);

    Assert.assertEquals(0, result.size());
  }

  @Test
  public void testMap()
  {
    Map<String,Object> map = new HashMap<>();

    map.put("foo", "mary had a little lamb");
    map.put("bar", "mary had two little lamb");
    map.put("zoo", "rose had three little lamb");

    map.put("age", 23);
    map.put("count", 32);

    _lucene.indexMap("foo", "map", map);

    applyChanges();

    List<LuceneEntry> result = _lucene.search("foo", "foo:lamb", 255);
    Assert.assertEquals(1, result.size());

    result = _lucene.search("foo", "bar:two", 255);
    Assert.assertEquals(1, result.size());

    result = _lucene.search("foo", "age:[23 TO 23]", 255);
    Assert.assertEquals(1, result.size());

    result = _lucene.search("foo", "count:32", 255);
    Assert.assertEquals(1, result.size());

    result = _lucene.search("foo", "count:[33 TO 34]", 255);
    Assert.assertEquals(0, result.size());
  }
}
