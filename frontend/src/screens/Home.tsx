import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useState } from 'react';
import apiCall from '@/lib/apiCall';
import { useToast } from '@/hooks/use-toast';

import GurpreetImage from '@/assets/Gurpreet.jpg';
import AkhilImage from '@/assets/Akhil.jpg';
import AumImage from '@/assets/Aum.jpg';
import NivasImage from '@/assets/Nivas.jpg';
import SaiImage from '@/assets/Sai.jpg';

const images = [
  { image: GurpreetImage, name: 'Gurpreet Singh' },
  { image: SaiImage, name: 'Sai Akhsay Indla' },
  { image: NivasImage, name: 'Nivas Verma' },
  { image: AkhilImage, name: 'Akhleswar Elluru' },
  { image: AumImage, name: 'Aum' },
];

const Home = () => {
  const [email, setEmail] = useState('');
  const { toast } = useToast();

  const onSubscribe = () => {
    apiCall
      .get('/subscribe_to_newsletter', { params: { email } })
      .then(res => {
        toast({
          title: res?.data?.match ? 'Successfully Subscribed' : 'Failed',
          description: res?.data?.match
            ? "You're in! Get ready for cool updates and occasional laughs"
            : "Oops! That email looks a bit wonky. Double-check it, and let's try again!",
        });
        console.log(res?.data?.match);
      })
      .catch(err => console.error(err));
  };
  return (
    <div className=" flex flex-col gap-5 h-[90vh]">
      <span className="bg-sky-900 bg-opacity-55 flex-1 text-4xl font-bold text-center flex flex-col justify-center">
        Cloud storage analyzer
      </span>

      <div className="flex flex-col justify-center flex-1 gap-8 flex-1">
        <span className="text-center text-3xl font-bold">Our Team</span>
        <div className="flex flex-row gap-5 justify-around">
          {images.map((e, index) => (
            <div>
              <img
                className="w-32 h-32 rounded-full object-cover"
                src={e?.image}
                alt={`Team member ${index + 1}`}
              />
              <span>{e?.name}</span>
            </div>
          ))}
        </div>
      </div>

      <div
        className="bg-sky-900 bg-opacity-25  flex-1  flex flex-col justify-center gap-2"
        style={{ alignItems: 'center' }}
      >
        <span className="text-xl font-bold text-center mb-2">
          Subscribe to our newsletter
        </span>

        <Input
          type="email"
          placeholder="abc@email.com"
          className="w-[50%]"
          value={email}
          onChange={e => setEmail(e?.target?.value || '')}
        />
        <Button onClick={onSubscribe}>Subscribe</Button>
      </div>
    </div>
  );
};

export default Home;
