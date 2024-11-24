import React from 'react';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from './ui/accordion';
import { ScrollArea } from './ui/scroll-area';

const PlanList = ({ data = [] }: { data: Array<{}> }) => {
  const headers = Object.keys(data?.[0] || {});
  return (
    <ScrollArea className="mt-4">
      <Accordion
        type="single"
        collapsible
        className="flex flex-1 flex-col gap-3 pb-2"
      >
        {data.map((e: any, i) => (
          <AccordionItem
            key={i + '_list'}
            value={i + '_list'}
            className="self-center w-[75%]"
          >
            <AccordionTrigger
              className={`flex ${'bg-neutral-800'} p-5 rounded-sm`}
            >
              <div className="flex flex-row flex-grow gap-2 mr-5">
                <div className="flex flex-1 flex-col gap-2">
                  <span className="font-bold text-xl capitalize">
                    {e?.Provider}
                  </span>

                  <span>{e?.Capacity}</span>
                </div>
                <div className="flex flex-col gap-2">
                  <span className="text-right">{`$${
                    e?.['Price per month'] || '0'
                  } CAD / month`}</span>
                  <span className="text-right">{`$${
                    e?.['Price per annum'] || '0'
                  } CAD / year`}</span>
                </div>
              </div>
            </AccordionTrigger>
            <AccordionContent
              className={`${'bg-neutral-600'} p-5 rounded-b-sm]`}
            >
              <span>{e?.paymentMethod}</span>
              <span>
                <ul>
                  {(e?.['Special features'].replaceAll('/n').split('- ') || [])
                    .filter((feat: string) => !!feat)
                    .map((feat: string) => (
                      <li className="flex gap-2">
                        <span>-</span>
                        {feat}
                      </li>
                    ))}
                </ul>
              </span>
            </AccordionContent>
          </AccordionItem>
        ))}
      </Accordion>
    </ScrollArea>
  );
};

export default PlanList;
