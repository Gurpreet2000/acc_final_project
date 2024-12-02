import { useEffect, useState } from 'react';
import SearchBar from '@/components/SearchBar';
import apiCall from '@/lib/apiCall';
import PlanList from '@/components/PlanList';
import MinMaxRangeSelector from '@/components/MinMaxRangeSelector';
import { Separator } from '@/components/ui/separator';
import { Button } from '@/components/ui/button';
import { parseCapacity } from '@/lib/utils';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

const Search = () => {
  const [currentTab, setCurrentTab] = useState('search');
  const [searchTerm, setSearchTerm] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [spellCheck, setSpellCheck] = useState('');
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [list, setList] = useState([]);
  const [capacityRange, setCapacity] = useState({ min: null, max: null });
  const [capacityList, setCapacityList] = useState<
    {
      label: string;
      value: any;
    }[]
  >([]);

  useEffect(() => {
    apiCall
      .get('/storage_list')
      .then(res => {
        const sortedList = res?.data?.data.map((e: any) => +e);
        sortedList.sort((a: number, b: number) => (a > b ? 1 : -1));

        setCapacityList(
          sortedList.map((e: number) => ({
            label: parseCapacity(+e),
            value: +e,
          }))
        );
      })
      .catch(err => {
        console.error(err);
      });
  }, []);

  useEffect(() => {
    if (!searchTerm) {
      setSuggestions([]);
      return;
    }

    apiCall
      .get('/auto_complete', {
        params: { q: searchTerm },
      })
      .then(res => setSuggestions(res?.data?.data))
      .catch(err => console.error(err));
  }, [searchTerm]);

  const onSearch = (query?: string) => {
    apiCall
      .get('/search', {
        params:
          currentTab === 'search'
            ? { q: query || searchTerm }
            : { minStorage: capacityRange.min, maxStorage: capacityRange.max },
      })
      .then(res => {
        setList(res?.data?.data);
        setSpellCheck(res?.data?.spellCheck);
      })
      .catch(err => console.error(err))
      .finally(() => {
        setSuggestions([]);
        setShowSuggestions(false);
      });
  };

  return (
    <div className="p-4">
      <Tabs
        defaultValue="search"
        onValueChange={setCurrentTab}
        className="flex flex-col"
      >
        <TabsList className="mb-2 self-center">
          <TabsTrigger value="search">Search</TabsTrigger>
          <TabsTrigger value="filter">Filter</TabsTrigger>
        </TabsList>
        <TabsContent value="search">
          <SearchBar
            data={suggestions}
            value={searchTerm}
            setValue={setSearchTerm}
            onSearch={onSearch}
            showSuggestions={showSuggestions}
            setShowSuggestions={setShowSuggestions}
          />
        </TabsContent>
        <TabsContent value="filter">
          <div
            className="flex flex-row justify-center gap-5"
            style={{ alignItems: 'center' }}
          >
            <MinMaxRangeSelector
              label="Select Capacity"
              setValue={setCapacity}
              value={capacityRange}
              minList={capacityList.filter(e =>
                capacityRange.max ? +e?.value <= +capacityRange.max : true
              )}
              maxList={capacityList.filter(e =>
                capacityRange.min ? +e?.value >= +capacityRange.min : true
              )}
            />
            <Button onClick={() => onSearch()}>Filter</Button>
          </div>
        </TabsContent>
      </Tabs>
      <Separator orientation="horizontal" className="my-3" />
      <div className="flex flex-col flex-1 align-middle mx-[10%]">
        {!!spellCheck && (
          <div className="mb-3">
            Do you mean:{' '}
            <a
              className="underline cursor-pointer"
              onClick={() => {
                setSearchTerm(spellCheck);
                onSearch(spellCheck);
              }}
            >
              {spellCheck}
            </a>
            ?
          </div>
        )}
        <PlanList data={list} />
      </div>
    </div>
  );

  return (
    <div className="p-4 flex h-full flex-row gap-5">
      <div className="flex flex-col gap-3 p-5"></div>
      <Separator orientation="vertical" />
    </div>
  );
};

export default Search;
