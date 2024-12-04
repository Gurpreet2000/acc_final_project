import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useState } from 'react';
import apiCall from '@/lib/apiCall';
import { useToast } from '@/hooks/use-toast';

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
    <div className=" flex flex-col gap-5">
      <span className="bg-sky-900 bg-opacity-55 h-[25vh] text-4xl font-bold text-center flex flex-col justify-center">
        Cloud storage analyzer
      </span>
      <div
        className="bg-sky-900 bg-opacity-25  h-[25vh]  flex flex-col justify-center gap-2"
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
