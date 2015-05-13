package com.caucho.lucene;

import io.baratine.core.Lookup;
import io.baratine.core.Result;
import io.baratine.core.ServiceRef;
import io.baratine.core.SessionService;
import io.baratine.session.SessionScoped;
import io.baratine.stream.StreamBuilder;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SessionService("session://lucene/session/{_id}")
public class LuceneSessionImpl implements LuceneSession
{
  @SessionScoped
  private String _id;

  @Inject @Lookup("pod://lucene/index")
  private LuceneIndex _index;

  @Inject @Lookup("pod://lucene/index")
  private ServiceRef _indexRef;

  private LuceneIndex getLuceneIndex(String id)
  {
    return _indexRef.node(id.hashCode()).as(LuceneIndex.class);
  }

  @Override
  public void indexFile(String collection, String path, Result<Boolean> result)
    throws LuceneException
  {
    getLuceneIndex(path).indexFile(collection, path, result);
  }

  @Override
  public void indexText(String collection,
                        String id,
                        String text,
                        Result<Boolean> result) throws LuceneException
  {
    getLuceneIndex(id).indexText(collection, id, text, result);
  }

  @Override
  public void indexMap(String collection,
                       String id,
                       Map<String,Object> map,
                       Result<Boolean> result) throws LuceneException
  {
    getLuceneIndex(id).indexMap(collection, id, map, result);
  }

  @Override
  public void search(String collection,
                     String query,
                     Result<LuceneEntry[]> result)
    throws LuceneException
  {
    StreamBuilder<LuceneEntry> stream = _index.search(collection, query);
    List<LuceneEntry> list
      = stream.collect(ArrayList<LuceneEntry>::new,
                       (l, e) -> l.add(e),
                       (a, b) -> a.addAll(b));

    result.complete(list.toArray(new LuceneEntry[list.size()]));
  }

  @Override
  public void delete(String collection, String id, Result<Boolean> result)
    throws LuceneException
  {
    getLuceneIndex(id).delete(collection, id, result);
  }

  @Override
  public void clear(String collection, Result<Void> result)
    throws LuceneException
  {
    _index.clear(collection, result);
  }
}
