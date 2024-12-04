import { useEffect, useState } from 'react';
import {
  Command,
  CommandEmpty,
  CommandList,
  CommandInput,
  CommandGroup,
  CommandItem,
} from './ui/command';
import { Button } from './ui/button';

export type SearchBarProps = {
  data: Array<string>;
  value: string;
  setValue: (value: string) => void;
  onSearch: () => void;
  showSuggestions: boolean;
  setShowSuggestions: (value: boolean) => void;
};

const SearchBar = ({
  data = [],
  value = '',
  setValue,
  onSearch = () => {},
  showSuggestions = false,
  setShowSuggestions = () => {},
}: SearchBarProps) => {
  const [debounceText, setDebounceText] = useState(value);
  useEffect(() => {
    const timeout = setTimeout(() => setValue(debounceText), 1000);

    return () => clearTimeout(timeout);
  }, [debounceText]);

  useEffect(() => {
    setDebounceText(value);
  }, [value]);

  return (
    <div className="flex flex-row gap-5 align-middle">
      <Command
        onFocus={() => setShowSuggestions(true)}
        onBlur={() => setShowSuggestions(false)}
        className="rounded-lg border shadow-md md:min-w-[450px] h-auto max-h-52"
      >
        <CommandInput
          placeholder="Search..."
          value={debounceText}
          onValueChange={setDebounceText}
        />
        <CommandList style={showSuggestions ? {} : { display: 'none' }}>
          <CommandEmpty>No results found.</CommandEmpty>
          <CommandGroup heading="Suggestions">
            {data.map(text => (
              <CommandItem
                key={text}
                onSelect={e => {
                  setDebounceText(e);
                  setShowSuggestions(false);
                  setValue(e);
                }}
              >
                {text}
              </CommandItem>
            ))}
          </CommandGroup>
        </CommandList>
      </Command>
      <Button
        onClick={() => {
          setValue(debounceText);
          onSearch();
        }}
      >
        Search
      </Button>
    </div>
  );
};

export default SearchBar;
